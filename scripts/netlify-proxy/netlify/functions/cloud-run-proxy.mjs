/**
 * Proxy autenticado hacia Cloud Run (ID token de cuenta de servicio).
 * Variables en Netlify: CLOUD_RUN_URL (https://...run.app sin / final), GCP_SA_JSON (JSON minificado).
 */
import { GoogleAuth } from "google-auth-library";

const PREFIX = "/backend-api";

function buildTargetUrl(cloudRunBase, event) {
  let path = event.path || "";
  if (path.startsWith("/.netlify/functions/cloud-run-proxy")) {
    path = path.slice("/.netlify/functions/cloud-run-proxy".length) || "/";
  }
  if (path.startsWith(PREFIX)) {
    path = path.slice(PREFIX.length) || "/";
  }
  if (!path.startsWith("/")) {
    path = `/${path}`;
  }
  const q = event.rawQuery ? `?${event.rawQuery}` : "";
  const base = cloudRunBase.replace(/\/$/, "");
  return `${base}${path}${q}`;
}

function normalizeHeaders(h) {
  if (!h) return {};
  const o = {};
  for (const [k, v] of Object.entries(h)) {
    if (v == null) continue;
    o[k] = Array.isArray(v) ? v.join(",") : String(v);
  }
  return o;
}

function filterRequestHeaders(headers) {
  const out = { ...headers };
  const drop = [
    "host",
    "connection",
    "content-length",
    "x-forwarded-host",
    "x-forwarded-proto",
    "x-forwarded-for",
    "x-nf-request-id",
  ];
  for (const k of drop) {
    delete out[k];
    delete out[k.toLowerCase()];
  }
  return out;
}

export const handler = async (event) => {
  if (event.httpMethod === "OPTIONS") {
    return {
      statusCode: 204,
      headers: {
        "Access-Control-Allow-Origin": event.headers.origin || "*",
        "Access-Control-Allow-Methods": "GET,POST,PUT,PATCH,DELETE,OPTIONS",
        "Access-Control-Allow-Headers":
          event.headers["access-control-request-headers"] ||
          "Authorization,Content-Type,X-Requested-With",
        "Access-Control-Max-Age": "86400",
      },
      body: "",
    };
  }

  const cloudRunBase = process.env.CLOUD_RUN_URL;
  const saJson = process.env.GCP_SA_JSON;
  if (!cloudRunBase || !saJson) {
    return {
      statusCode: 500,
      body: JSON.stringify({
        error: "Missing CLOUD_RUN_URL or GCP_SA_JSON in Netlify environment",
      }),
      headers: { "Content-Type": "application/json" },
    };
  }

  let credentials;
  try {
    credentials = JSON.parse(saJson);
  } catch {
    return {
      statusCode: 500,
      body: JSON.stringify({ error: "GCP_SA_JSON is not valid JSON" }),
      headers: { "Content-Type": "application/json" },
    };
  }

  const targetUrl = buildTargetUrl(cloudRunBase, event);
  const auth = new GoogleAuth({ credentials });
  const idClient = await auth.getIdTokenClient(cloudRunBase.replace(/\/$/, ""));

  const headers = filterRequestHeaders(event.headers || {});
  let data;
  if (event.body && event.httpMethod !== "GET" && event.httpMethod !== "HEAD") {
    data = event.isBase64Encoded ? Buffer.from(event.body, "base64") : event.body;
  }

  try {
    const res = await idClient.request({
      url: targetUrl,
      method: event.httpMethod,
      headers,
      data,
    });
    const payload = res.data;
    const body =
      typeof payload === "string"
        ? payload
        : payload !== undefined
          ? JSON.stringify(payload)
          : "";
    const outHeaders = normalizeHeaders(res.headers);
    if (event.headers?.origin) {
      outHeaders["access-control-allow-origin"] = event.headers.origin;
      outHeaders["access-control-allow-credentials"] = "true";
    }
    return {
      statusCode: res.status || 200,
      headers: outHeaders,
      body,
    };
  } catch (err) {
    const status = err.response?.status || 502;
    const payload = err.response?.data || { message: err.message };
    return {
      statusCode: status,
      headers: { "Content-Type": "application/json" },
      body: typeof payload === "string" ? payload : JSON.stringify(payload),
    };
  }
};
