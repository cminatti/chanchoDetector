# chanchoDetector

**chanchoDetector** es una aplicación Android especializada para drones DJI (Enterprise), diseñada para la detección y monitoreo de objetos en movimiento, con un enfoque específico en la identificación de fauna (porcinos) en entornos rurales o de campo.

## Objetivo

El objetivo principal del proyecto es proporcionar a los operadores de drones una herramienta capaz de analizar el flujo de video en tiempo real para detectar actividad y movimiento de animales automáticamente. Esto permite optimizar las tareas de vigilancia, conteo y monitoreo sin depender exclusivamente de la observación manual continua por parte del piloto.

## Estado del Proyecto

Actualmente, el proyecto se encuentra en su **versión funcional y estable (v1.0)**. 
- La integración con el SDK de DJI es robusta.
- El sistema de detección base por movimiento es operativo.
- La arquitectura está preparada para la escalabilidad hacia modelos de Inteligencia Artificial avanzados.

## Tecnologías Utilizadas

- **Android Studio**: Entorno de desarrollo integrado (IDE).
- **Kotlin**: Lenguaje de programación principal.
- **DJI Mobile SDK V5**: Librería oficial para la comunicación y control de aeronaves DJI serie Enterprise.
- **Gradle**: Sistema de gestión de dependencias y construcción.
- **Android SDK (API 35)**: Base del sistema operativo.
- **Maplibre**: Motor de mapas integrado para visualización geoespacial.
- **View Binding**: Para una interacción segura y eficiente con la interfaz de usuario.

## Arquitectura

El proyecto sigue una arquitectura modular y desacoplada basada en los siguientes componentes:

### Módulos Principales
- **`:sample`**: Contiene la lógica de la aplicación, las actividades personalizadas y el flujo principal del usuario.
- **`:uxsdk`**: Librería de componentes visuales (widgets) de DJI para control de cámara, batería y estado del dron.

### Desacoplamiento de Procesamiento
Se ha implementado el patrón de diseño a través de la interfaz **`FrameProcessor`**.
- **Propósito**: Desacoplar la lógica de análisis de imágenes de la interfaz de usuario (`ChanchoDetectorActivity`).
- **Ventaja**: Permite intercambiar el algoritmo de detección (ej. cambiar Detección de Movimiento por YOLO Nano) sin modificar el código de la actividad, facilitando el mantenimiento y las pruebas.

## Funcionalidades Implementadas

- **Transmisión de Video en Vivo**: Visualización en tiempo real de la cámara del dron.
- **Detección de Movimiento**: Identificación de cambios en los frames de video para marcar regiones de interés.
- **Overlay de Detección**: Dibujo de cuadros verdes en pantalla sobre los objetos detectados.
- **Alertas Hápticas**: El dispositivo vibra automáticamente al detectar movimiento confirmado en el área.
- **Gestión de Ciclo de Vida**: Inicialización y liberación automática de recursos del SDK de DJI para evitar fugas de memoria.
- **Navegación Integrada**: Acceso a herramientas de diagnóstico y control oficial de DJI.

## Flujo de Funcionamiento

1. **Inicio**: Al abrir la app, se inicia el proceso de registro automático del SDK de DJI.
2. **Conexión**: Se establece el enlace con el control remoto y la aeronave.
3. **Activación**: El usuario presiona el botón "CHANCHO DETECTOR" en la pantalla principal.
4. **Análisis**: La app comienza a recibir frames YUV del dron.
5. **Detección**: El `FrameProcessor` analiza el movimiento y actualiza el overlay visual y las alertas de vibración.

## Requisitos

- **Android**: Mínimo Android 7.0 (API 24).
- **Arquitectura**: Dispositivo con procesador ARM64-v8a (Requerido por MSDK V5).
- **Hardware**: Dron DJI compatible con SDK v5 (Mavic 3E, Matrice 30, etc.).
- **Permisos**: Ubicación (Fine/Coarse), Almacenamiento, Micrófono, Vibración y Acceso USB.

## Compilación

Para compilar el proyecto:
1. Abrir en **Android Studio Ladybug (o superior)**.
2. Sincronizar Gradle.
3. Ejecutar el comando:
   ```bash
   ./gradlew :sample:assembleDebug
   ```

## Ejecución

1. Conectar un dispositivo físico Android (ARM64) al control remoto de DJI.
2. Desplegar el módulo `:sample`.
3. Otorgar los permisos solicitados en la primera ejecución.
4. **Nota**: El video no funcionará en emuladores debido a la falta de hardware de decodificación y comunicación de DJI.

## Roadmap (Próximas Implementaciones)

- **Integración de YOLO Nano**: Implementación de un `FrameProcessor` basado en redes neuronales para detección específica de chanchos.
- **Optimización del Procesamiento**: Mejora en los tiempos de inferencia y reducción de consumo de batería.
- **Mejoras de Rendimiento**: Uso de corrutinas para el análisis de frames en hilos dedicados.
- **Mejoras de Seguridad**: Migración de credenciales a un sistema de gestión de secretos seguro.
- **Documentación Técnica**: Ampliación de la documentación de la API interna.
- **Optimización de Campo**: Ajustes de UI para alta visibilidad bajo luz solar directa.

## Seguridad

Actualmente, las credenciales del SDK (API Keys, Tokens) y la configuración de la Keystore permanecen en archivos de configuración (`gradle.properties`) por motivos de agilidad en el desarrollo. 

> [!NOTE]
> La migración hacia una gestión segura de secretos (ej. `secrets.properties` ignorado por Git o variables de entorno) es una tarea planificada en el roadmap de endurecimiento de seguridad previo a una publicación en repositorios públicos masivos.

## Créditos

Este proyecto se desarrolla de forma colaborativa integrando las capacidades del MSDK V5 de DJI con lógica de procesamiento de imagen personalizada.

## Licencia

Actualmente no existe una licencia definida para este proyecto derivado. Se recomienda consultar los términos del DJI SDK antes de cualquier uso comercial.
