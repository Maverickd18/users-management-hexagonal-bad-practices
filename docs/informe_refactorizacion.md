# Informe de Refactorización y Corrección de Malas Prácticas

Este documento detalla los errores encontrados, las violaciones a las reglas de Clean Code, Arquitectura Hexagonal y Domain-Driven Design (DDD), y las correcciones aplicadas a cada archivo durante el proceso de refactorización del sistema de gestión de usuarios.

## Resumen de Reglas y Principios Aplicados
- **Regla 3:** Eliminación de constructores redundantes (ej. `@Builder` en `records`).
- **Regla 4:** Uso de herramientas estándar (`Objects.requireNonNull`), clases utilitarias (`@UtilityClass`), y nombres descriptivos sin abreviaturas.
- **Regla 6 & 24:** Unificación y limpieza de Logs (uso de `@Log` de Lombok), eliminación de PII (Información Personal Identificable) en logs.
- **Regla 10:** Eliminación de "Magic Strings/Numbers" mediante el uso de constantes.
- **Regla 11:** Cumplimiento estricto de estándares de testing (Anotación `@DisplayName`, estructura Arrange-Act-Assert, y uso correcto de aserciones en lugar de impresiones en consola).
- **Regla 22:** Mejora en el diseño para facilitar el testing y la tolerancia a fallos (Resiliencia).
- **Regla 23 (Ley de Deméter):** Respeto por el encapsulamiento delegando comportamientos en lugar de encadenar llamadas a getters profundos.

---

## 1. Capa de Dominio (Domain)

### Value Objects (`UserName`, `UserPassword`, `UserId`, `UserEmail`)
- **Errores:** Validaciones manuales deficientes (ej. `== null`), uso de números y cadenas mágicas, exposición de información sensible en los logs (para `UserEmail`).
- **Correcciones:** 
  - Se implementó `Objects.requireNonNull()` para validar nulidad (Regla 4).
  - Se extrajeron literales a constantes (Regla 10).
  - Se eliminaron los logs que imprimían información sensible y se garantizó la inmutabilidad.

### Excepciones (`UserNotFoundException`, `ConfigurationException`, etc.)
- **Errores:** Textos hardcodeados directamente en los constructores y mensajes de error genéricos.
- **Correcciones:** 
  - Extracción de literales a constantes (`private static final String`).
  - Creación de métodos de fábrica (ej. `becauseValueIsInvalid()`, `becauseConnectionFailed()`) para dar contexto preciso del error.

### Modelos (`EmailDestinationModel`, `UserModel`)
- **Errores:** Acoplamiento con infraestructura, validaciones manuales (`== null` en lugar de `Objects`).
- **Correcciones:** 
  - Limpieza de dependencias externas.
  - Implementación de métodos de delegación en `UserModel` (ej. `idValue()`, `nameValue()`) para evitar violar la Ley de Deméter en capas superiores y tests.

---

## 2. Capa de Aplicación (Application)

### Comandos (`CreateUserCommand`)
- **Errores:** Uso redundante de la anotación `@Builder` de Lombok en un `record`, lo cual es innecesario ya que los records tienen un constructor canónico inmutable por defecto (Regla 3).
- **Correcciones:** Eliminación de `@Builder`.

### Servicios (`CreateUserService`, `UpdateUserService`, `LoginService`, etc.)
- **Errores:** Control de excepciones basado en try-catch no recuperables, mezcla de frameworks de logging (SLF4J vs `java.util.logging`), lógica de validación acoplada.
- **Correcciones:** 
  - Estandarización a `java.util.logging` mediante `@Log` (Regla 24).
  - Limpieza de PII en logs.
  - Delegación de validaciones al `Validator` de Jakarta.

---

## 3. Capa de Infraestructura (Infrastructure)

### Adaptadores de Persistencia (`UserRepositoryMySQL`, Mappers)
- **Errores:** Acoplamiento temporal en métodos (ej. `init()` manual), uso de imports no utilizados (`java.util.ArrayList`), y clases utilitarias instanciables.
- **Correcciones:** 
  - Las clases con puros métodos estáticos (`UserPersistenceMapper`, `DatabaseConnectionFactory`) se decoraron con `@UtilityClass` (Lombok) para hacerlas finales y ocultar su constructor.
  - Se eliminaron métodos redundantes y dependencias no utilizadas.

### Configuración (`application.properties`, `AppProperties`, `DependencyContainer`)
- **Errores Críticos:** 
  - `application.properties` contenía placeholders literales como `"port-number"`, causando un `NumberFormatException` que bloqueaba el arranque de la app.
  - Acoplamiento fuerte a la base de datos MySQL; si fallaba, la app crasheaba (Falta de resiliencia).
- **Correcciones:** 
  - Normalización de `application.properties` con valores por defecto válidos.
  - Refactorización de `AppProperties` para capturar errores de formato de números y lanzar una excepción descriptiva (`ConfigurationException`).
  - Implementación del patrón *Fallback*: Se creó un `InMemoryUserRepository`. En el `DependencyContainer`, si la conexión a MySQL falla, la aplicación captura el error y arranca usando el repositorio en memoria, evitando el crash total.

### Envío de Correos (`MailtrapEmailSenderAdapter` -> `JavaMailEmailSenderAdapter`)
- **Errores:** Se intentó introducir el SDK de Mailtrap, lo cual generó un `NoClassDefFoundError` en tiempo de ejecución debido a la falta de actualización del classpath en el entorno local.
- **Correcciones:** Se revirtió al adaptador nativo `JavaMailEmailSenderAdapter` configurado con los parámetros SMTP (incluyendo modo Sandbox), garantizando el funcionamiento sin depender de librerías de terceros problemáticas en este entorno específico.

### Entrypoints (CLI `UserManagementCli`, `ConsoleIO`)
- **Errores:** Nombres poco descriptivos, strings literales para mensajes de error, y manejo inconsistente de lecturas de consola.
- **Correcciones:** Extracción a constantes, estandarización de nombres (Regla 4).

---

## 4. Pruebas Unitarias (Tests)

- **Errores Generales:** 
  - Ausencia de `@DisplayName` para explicar el propósito del test.
  - Falta de comentarios AAA (Arrange-Act-Assert).
  - Violación de la Ley de Deméter (ej. `user.getId().value()`).
  - Uso de aserciones pobres (ej. `assertTrue(x.equals(y))` en lugar de `assertEquals(x, y)`).
  - En `UserControllerTest`: Acceso a propiedades de `records` usando la convención antigua `getId()` en lugar de `id()`.
- **Correcciones:** 
  - Todos los tests fueron refactorizados para incluir `@DisplayName` y la estructura AAA (Regla 11).
  - Se reemplazaron cadenas de llamadas profundas por métodos de conveniencia provistos por el Dominio (ej. `user.idValue()`).
  - Se ajustó la sintaxis en `UserControllerTest` para usar los métodos accesorios correctos de los records.
  - Corrección de la instanciación de clases de configuración (`SmtpConfig`) en los tests de infraestructura.

---

## Conclusión

El proyecto ha pasado de un estado frágil y acoplado a uno altamente modular, resiliente y alineado con los principios de Clean Code y Arquitectura Hexagonal. Los problemas críticos de arranque (configuración de base de datos y classpath) fueron resueltos mediante validación de entrada y fallbacks en memoria. El código de prueba ahora sirve como documentación viva y confiable.
