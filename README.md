# Rick and Morty App 🧪🚀

Aplicación Android desarrollada en **Kotlin**, basada en la arquitectura **Clean Architecture + MVVM**, que consume la API pública de [Rick and Morty API](https://rickandmortyapi.com/).

Su objetivo es mostrar un listado paginado de personajes de la serie y visualizar el detalle de cada personaje, siguiendo las mejores prácticas de arquitectura, testing, accesibilidad y diseño.

---

## 🧩 Arquitectura del Proyecto

El proyecto está modularizado para mantener una estructura escalable, limpia y fácilmente testeable:

App/
Core/
├── common/
├── di/
├── navigation/
└── ui/
Data/
Domain/
Features/
├── splash/
├── characterslist/
└── characterdetail/


### Descripción de módulos

| Módulo | Descripción |
|---------|--------------|
| **App** | Punto de entrada principal. Configura la inyección de dependencias y la inicialización general. |
| **Core** | Contiene las utilidades comunes, configuración de DI con **Koin**, navegación, componentes UI compartidos y clases base. |
| **Data** | Gestiona la obtención y persistencia de datos. Usa **Retrofit** para red, **Room** como caché local y **Paging 3** para paginación. |
| **Domain** | Define los **casos de uso (UseCases)** y modelos de dominio independientes de la capa de datos. |
| **Features** | Cada funcionalidad independiente de la app (Splash, Listado, Detalle) con su propio ViewModel, UI y navegación. |

---

## ⚙️ Stack Tecnológico

| Componente | Librería / Tecnología |
|-------------|-----------------------|
| Lenguaje | Kotlin |
| Arquitectura | Clean Architecture + MVVM |
| Inyección de dependencias | [Koin](https://insert-koin.io/) |
| Peticiones HTTP | [Retrofit](https://square.github.io/retrofit/) |
| Serialización | [Moshi](https://github.com/square/moshi) |
| Caché local | [Room](https://developer.android.com/training/data-storage/room) |
| Imágenes | [Coil](https://coil-kt.github.io/coil/) |
| Paginación | [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) |
| Asincronía | Kotlin Coroutines + Flow |
| UI | Jetpack Compose |
| Testing | JUnit5 + Mockito |

---

## 🚀 Flujo de la aplicación

1. **Splash Screen:** muestra la animación inicial con los colores característicos de Rick y Morty.
2. **Listado de personajes:** paginado con soporte de filtro (nombre, estado, especie, etc.).
3. **Detalle del personaje:** información completa del personaje seleccionado (imagen, especie, estado, origen, etc.).
4. **Cacheo local:** los datos consultados se almacenan en Room, permitiendo acceder sin conexión.
5. **Prefetching:** optimiza la carga anticipada de páginas para mejorar la experiencia de scroll.

---

## 🧪 Testing

El proyecto cuenta con **tests unitarios** centrados en las capas **Data** y **Domain**, usando:

- **JUnit 5** para la ejecución de pruebas.
- **Mockito** para mocks y verificaciones.
- **Factory Tests** para generar datos simulados.
- Cobertura enfocada en UseCases, Repositorios y Mappers.

---

## 🎨 Diseño y Accesibilidad

- Inspirado en la estética original de **Rick and Morty**, con los colores verde portal y azul celeste.
- Uso de **Jetpack Compose** con enfoque en accesibilidad: roles semánticos, descripciones y contraste de color.
- Imagen de icono creada específicamente para el proyecto (Rick y Morty saliendo del portal).
- Textos localizados en **español e inglés** mediante archivos `strings.xml` separados.

---

## 🧠 Buenas Prácticas

- Código **simple, legible y testeable**.
- Funciones pequeñas con **una sola responsabilidad (SRP)**.
- Arquitectura modular desacoplada.
- Gestión de errores controlada mediante un sistema **DataResult<AppError>** y el propio de Paging3.
- Uso correcto del ciclo de vida en ViewModels y Flows.
