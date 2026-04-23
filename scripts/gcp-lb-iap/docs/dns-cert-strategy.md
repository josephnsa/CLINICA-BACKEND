# Decision de DNS y certificado (LB + IAP)

## Contexto

Para LB + IAP se requiere un host HTTPS estable para navegador corporativo.

## Estrategia recomendada

1. **Host objetivo**: `api.axelior.ai` (o subdominio definido por Infra).
2. **Certificado**: `gcloud compute ssl-certificates` administrado por Google.
3. **DNS**: registro `A` del host al IP global del forwarding rule.
4. **Propagación**: esperar estado `ACTIVE` del certificado antes del cutover de frontend.

## Plan por fases

### Fase temporal (si DNS aún no está listo)

- Crear todos los recursos LB/IAP.
- Reservar/obtener IP global del forwarding rule.
- Mantener frontend apuntando al endpoint previo.

### Fase final (cutover)

- Aplicar DNS `A` definitivo.
- Confirmar cert activo.
- Cambiar `apiUrl` del frontend a `https://api.axelior.ai/api`.

## Checklist de aceptación

- [ ] DNS resuelve al IP del LB.
- [ ] Certificado en estado `ACTIVE`.
- [ ] IAP habilitado y miembros corporativos autorizados.
- [ ] `run.app` sigue no público.
