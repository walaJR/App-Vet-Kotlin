# Sistema de Gestión Veterinaria

Aplicación Android nativa desarrollada en Kotlin para la gestión integral de mascotas, dueños, veterinarios y consultas médicas.

## Características Principales

- **Registro de Mascotas**: Gestión completa de información de mascotas y sus dueños
- **Gestión de Consultas**: Registro, seguimiento y compartir historial médico
- **Listado Inteligente**: Visualización por pestañas (Mascotas/Consultas) con filtros
- **Cálculos Automáticos**: Sistema de costos con descuentos por múltiples consultas
- **Recordatorios**: Servicio de notificaciones para consultas pendientes
- **Sistema de Login**: Autenticación local con recuperación de contraseña

## Stack Tecnológico

### Core
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material Design 3
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room Persistence Library

### Librerías Clave
- **Asincronía**: Kotlin Coroutines + StateFlow
- **Imágenes**: Glide 4.16.0 (carga asíncrona y caché)
- **Fechas**: ThreeTenABP
- **Debugging**: LeakCanary 2.13 (detección de memory leaks)
- **HTTP**: Retrofit 2.9.0 (preparado para APIs REST)

### Características Técnicas Avanzadas
- Operaciones asíncronas sin bloquear UI
- Manejo robusto de errores con logs estructurados
- Zero memory leaks (verificado con LeakCanary)
- Arquitectura escalable con repositorios
- Estados reactivos con Flow/StateFlow
- Animaciones fluidas con Compose
