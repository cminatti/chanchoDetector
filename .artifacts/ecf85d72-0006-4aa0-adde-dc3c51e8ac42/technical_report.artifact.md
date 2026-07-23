# Informe Técnico de Auditoría - Proyecto chanchoDetector

Este informe detalla el estado actual del proyecto, identificando riesgos técnicos, de seguridad y áreas de mejora arquitectónica.

## 1. Configuración de Build y Compatibilidad

| Archivo | Gravedad | Hallazgo | Riesgo | Solución Recomendada |
| :--- | :--- | :--- | :--- | :--- |
| `build.gradle` (Raíz) | **Media** | Uso de `jcenter()` en repositorios. | JCenter es solo lectura y a menudo falla o es inestable. | Migrar completamente a `mavenCentral()` y `google()`. |
| `gradle.properties` | **Alta** | Secretos expuestos (`STORE_PASSWORD`, `KEY_PASSWORD`, `AIRCRAFT_API_KEY`). | Si este archivo se sube a Git, las credenciales quedan comprometidas. | Usar variables de entorno o un archivo `secrets.properties` fuera de Git. |
| `gradle.properties` | **Baja** | `GMAP_API_KEY = UNSET`. | Google Maps no cargará en la app. | Configurar una API Key válida de Google Cloud Console. |
| `build.gradle` (:sample) | **Baja** | `JavaVersion.VERSION_1_8` con SDK 35. | Desaprovecha optimizaciones de versiones más modernas de Java. | Subir a `JavaVersion.VERSION_11` o `17`. |

---

## 2. Manifiesto y Permisos

| Archivo | Gravedad | Hallazgo | Riesgo | Solución Recomendada |
| :--- | :--- | :--- | :--- | :--- |
| `AndroidManifest.xml` | **Crítica** | `android:extractNativeLibs="true"`. | Aumenta significativamente el tamaño de la app instalada. | Cambiar a `false` (requiere `useLegacyPackaging = false` en Gradle). |
| `AndroidManifest.xml` | **Alta** | `MANAGE_EXTERNAL_STORAGE`. | Permiso intrusivo que Play Store suele rechazar. | Usar `MediaStore` o permisos específicos por tipo de archivo. |
| `AndroidManifest.xml` | **Baja** | Permisos duplicados en `:uxsdk` y `:sample`. | Ruido innecesario en el merge del manifiesto. | Centralizar permisos comunes en el módulo base o librería. |
| `AndroidManifest.xml` | **Media** | `android:allowBackup="true"`. | Riesgo de seguridad; datos de la app pueden extraerse vía ADB. | Cambiar a `false` a menos que sea estrictamente necesario. |

---

## 3. Arquitectura y Calidad de Código

| Archivo | Gravedad | Hallazgo | Riesgo | Solución Recomendada |
| :--- | :--- | :--- | :--- | :--- |
| `MotionDetector.kt` | **Media** | Acceso a `prevFrame` sin sincronización. | Posible condición de carrera si el listener de DJI dispara en paralelo. | Usar `synchronized` o una estructura thread-safe para el buffer del frame. |
| `ChanchoDetectorActivity.kt` | **Baja** | Uso de `lateinit var binding`. | Posible `UninitializedPropertyAccessException` si se accede antes de `onCreate`. | Usar el patrón de backing property nula para `_binding`. |
| `DJIAircraftMainActivity.kt` | **Baja** | Subclase vacía. | Código muerto que añade complejidad innecesaria. | Eliminar si no añade funcionalidad sobre `DJIMainActivity`. |
| General | **Media** | Uso excesivo del operador `!!`. | Riesgo de `NullPointerException` en tiempo de ejecución. | Usar `safe calls (?.)` o `let`. |

---

## 4. Seguridad y Gestión de Archivos

| Archivo | Gravedad | Hallazgo | Riesgo | Solución Recomendada |
| :--- | :--- | :--- | :--- | :--- |
| `.gitignore` | **Alta** | `*.jks` está comentado en el gitignore. | El archivo `msdkkeystore.jks` podría subirse a Git. | Descomentar `*.jks` y asegurar que el keystore no esté trackeado. |
| `gradle.properties` | **Media** | `AIRCRAFT_API_KEY` hardcoded. | Exposición de la llave de DJI. | Mover a un lugar seguro o usar inyección de Git secrets en CI/CD. |

---

## 5. Rendimiento y Recursos

| Hallazgo | Gravedad | Riesgo | Solución Recomendada |
| :--- | :--- | :--- | :--- |
| Procesamiento de frames en el hilo de DJI. | **Baja** | Si el análisis es pesado, puede retrasar la recepción de nuevos frames. | Mover el procesamiento pesado a un `CoroutineDispatcher` dedicado (Default). |
| Redimensionamiento manual en `downsample`. | **Media** | Ineficiencia computacional. | Usar librerías optimizadas o aprovechar funciones de RenderScript/Intrinsics para escalado. |

---

> [!IMPORTANT]
> **Prioridad Inmediata**: Corregir la exposición de la Keystore y las contraseñas en `gradle.properties` antes de realizar cualquier commit.
