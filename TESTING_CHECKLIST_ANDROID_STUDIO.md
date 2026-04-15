# WorldScope - Checklist de pruebas

## Smoke
- [ ] App abre sin crash.
- [ ] Navegacion inferior funciona en todas las pestañas.
- [ ] Volver desde detalle funciona.

## Explorador
- [ ] Busqueda filtra por nombre.
- [ ] Filtro por region funciona.
- [ ] Orden por nombre, poblacion y superficie funciona.
- [ ] Cambio de vista lista/grid funciona.
- [ ] Chips de busquedas recientes se aplican al pulsar.
- [ ] Chips de paises recientes abren detalle correcto.

## Detalle
- [ ] Carga de datos base (capital, region, codigos, area).
- [ ] Boton favorito cambia estado.
- [ ] Boton abrir mapa abre app de mapas.
- [ ] Secciones clima/divisa/economia/wikipedia muestran dato o fallback.

## Comparador
- [ ] Validacion de seleccion de 2 paises.
- [ ] Compara poblacion, area, capital, region, PIB, inflacion, moneda y clima.
- [ ] Boton limpiar comparacion limpia estado.

## Quiz
- [ ] Genera pregunta.
- [ ] Permite responder una vez por ronda.
- [ ] Siguiente ronda funciona.
- [ ] Reinicio de puntuacion funciona.

## Favoritos y persistencia
- [ ] Añadir favorito desde detalle.
- [ ] Quitar favorito desde detalle y lista favoritos.
- [ ] Persistencia al reiniciar app.
- [ ] Historial de busquedas persiste al reiniciar.

## Red y errores
- [ ] Sin API keys: app no crashea y muestra fallback.
- [ ] Sin internet: se muestran errores de red en pantallas de datos.
- [ ] Reintentar en error recupera al volver internet.
