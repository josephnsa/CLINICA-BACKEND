/**
 * BFF en Cloud Run: reenvía al backend salud-backend usando ID token (ADC / metadata).
 * Sin clave JSON: la identidad es la service account del propio servicio (--service-account).
 *
 * Env: SALUD_BACKEND_URL (https://salud-backend-....run.app sin barra final)
 *      PORT (Cloud Run lo inyecta; por defecto 8080)
 */
import http from "node:http";
import { GoogleAuth } from "google-auth-library";

const targetBase = (process.env.SALUD_BACKEND_URL || "").replace(/\/$/, "");
const port = Number(process.env.PORT) || 8080;

if (!targetBase) {
  console.error("FATAL: SALUD_BACKEND_URL no definida");
  process.exit(1);
}

const hopByHop = new Set([
  "connection",
  "keep-alive",
  "proxy-authenticate",
  "proxy-authorization",
  "te",
  "trailers",
  "transfer-encoding",
  "upgrade",
  "host",
  "content-length",
]);

function pickHeaders(incoming) {
  const out = {};
  for (const [k, v] of Object.entries(incoming)) {
    if (!k || hopByHop.has(k.toLowerCase())) continue;
    if (v === undefined) continue;
    out[k] = Array.isArray(v) ? v.join(",") : v;
  }
  return out;
}

function flattenResponseHeaders(h) {
  if (!h) return {};
  const o = {};
  for (const [k, v] of Object.entries(h)) {
    if (v == null) continue;
    o[k] = Array.isArray(v) ? v.join(",") : String(v);
  }
  return o;
}

const auth = new GoogleAuth();

const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") {
    res.writeHead(204, {
      "Access-Control-Allow-Origin": req.headers.origin || "*",
      "Access-Control-Allow-Methods": "GET,POST,PUT,PATCH,DELETE,OPTIONS",
      "Access-Control-Allow-Headers":
        req.headers["access-control-request-headers"] ||
        "Authorization,Content-Type,X-Requested-With",
      "Access-Control-Max-Age": "86400",
      "Access-Control-Allow-Credentials": "true",
    });
    res.end();
    return;
  }

  const url = `${targetBase}${req.url || "/"}`;
  const chunks = [];
  for await (const c of req) {
    chunks.push(c);
  }
  const bodyBuf = Buffer.concat(chunks);

  try {
    const idClient = await auth.getIdTokenClient(targetBase);
    const headers = pickHeaders(req.headers);
    const upstream = await idClient.request({
      url,
      method: req.method,
      headers,
      data: bodyBuf.length ? bodyBuf : undefined,
      responseType: "text",
      validateStatus: () => true,
    });

    const rh = flattenResponseHeaders(upstream.headers);
    delete rh["access-control-allow-origin"];
    delete rh["Access-Control-Allow-Origin"];
    if (req.headers.origin) {
      rh["Access-Control-Allow-Origin"] = req.headers.origin;
      rh["Access-Control-Allow-Credentials"] = "true";
    }

    const payload =
      typeof upstream.data === "string"
        ? upstream.data
        : upstream.data != null
          ? JSON.stringify(upstream.data)
          : "";

    res.writeHead(upstream.status, rh);
    res.end(payload);
  } catch (err) {
    const msg = err.response?.data || err.message || String(err);
    res.writeHead(502, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ error: "bff_upstream", detail: String(msg) }));
  }
});

server.listen(port, () => {
  console.log(`BFF escuchando :${port} → ${targetBase}`);
});
