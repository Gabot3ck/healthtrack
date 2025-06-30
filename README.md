# HealthTrack Platform

Una plataforma web para el monitoreo del peso de los usuarios con sistema de validación automática y pipeline de CI/CD.

## Descripción del Proyecto

HealthTrack es una aplicación desarrollada en Java 17 que permite a los usuarios registrarse y actualizar su peso cada 48 horas. El proyecto incluye un sistema completo de testing automatizado que detecta y previene errores críticos en la lógica de negocio.

### Problema Original Resuelto

El sistema tenía un **bug crítico** donde cada vez que un usuario actualizaba su peso, el sistema le restaba automáticamente 1 kg en lugar de registrar el valor ingresado:

```java
// CÓDIGO ORIGINAL (BUGUEADO)
public void actualizarPeso(double nuevoPeso) {
    this.peso -= 1;  // ❌ Error: resta 1kg en lugar de asignar
}

// CÓDIGO CORREGIDO
public void actualizarPeso(double nuevoPeso) {
    this.peso = nuevoPeso;  // ✅ Correcto: asigna el nuevo peso
}
```

## Tecnologías Utilizadas

- **Java 17** - Lenguaje principal
- **Maven** - Gestión de dependencias y build
- **JUnit 5** - Framework de testing unitario
- **Mockito** - Framework de mocking
- **AssertJ** - Assertions fluidas para testing
- **Selenium WebDriver** - Tests funcionales automatizados
- **JaCoCo** - Análisis de cobertura de código
- **GitHub Actions** - CI/CD Pipeline

## Arquitectura del Proyecto

```
healthtrack-platform/
├── src/
│   ├── main/java/com/healthtrack/healthtrack_platform/
│   │   └── model/
│   │       └── Usuario.java
│   └── test/java/com/healthtrack/healthtrack_platform/
│       ├── model/
│       │   └── UsuarioTest.java
│       ├── performance/
│       │   └── UsuarioPerformanceTest.java
│       └── functional/
│           ├── UsuarioFunctionalTest.java
│           ├── UsuarioFunctionalPOMTest.java
│           ├── config/
│           │   └── TestConfig.java
│           └── pages/
│               └── HealthTrackPage.java
├── .github/workflows/
│   └── ci.yml
├── pom.xml
└── README.md
```

## Características Principales

### Funcionalidades de Negocio
- ✅ Registro de usuarios con validaciones
- ✅ Actualización de peso cada 48 horas
- ✅ Validación de datos de entrada
- ✅ Historial de actualizaciones
- ✅ Interfaz web intuitiva

### Sistema de Testing
- ✅ **Tests Unitarios** - Validación de lógica de negocio
- ✅ **Tests de Performance** - Evaluación de rendimiento
- ✅ **Tests Funcionales** - Simulación de usuario real con Selenium
- ✅ **Detección de Bugs** - Tests específicos para detectar el error original
- ✅ **Cobertura de Código** - Análisis con JaCoCo

### CI/CD Pipeline
- ✅ **Integración Continua** con GitHub Actions
- ✅ **Ejecución Automática** de todos los tipos de tests
- ✅ **Reportes de Cobertura** automatizados
- ✅ **Artefactos de Build** generados automáticamente
- ✅ **Comentarios en PRs** con resultados de tests

## Instalación y Configuración

### Prerequisitos
- Java 17 o superior
- Maven 3.6+ 
- Git
- Chrome/Firefox (para tests funcionales)

### Clonar el Repositorio
```bash
git clone https://github.com/Gabot3ck/healthtrack-platform.git
cd healthtrack-platform
```

### Instalar Dependencias
```bash
mvn clean install
```

## Ejecución de Tests

### Tests Unitarios
```bash
# Ejecutar tests unitarios
mvn test

# Con reporte de cobertura
mvn clean test jacoco:report
```

### Tests de Performance
```bash
mvn test -Pperformance-tests
```

### Tests Funcionales
```bash
# Con navegador headless (CI)
mvn test -Pfunctional-tests

# Con interfaz gráfica (debugging)
mvn test -Pfunctional-tests -Dtest.headless=false

# Con Firefox
mvn test -Pfunctional-tests -Dtest.browser=firefox
```

### Suite Completa de Tests
```bash
# Todos los tests + build
mvn clean verify

# Pipeline completo local
./scripts/test-pipeline.sh  # Si tienes el script
```

## Análisis de Cobertura

### Generar Reportes
```bash
mvn clean test jacoco:report
```

### Ver Reportes
```bash
# Abrir reporte en navegador
open target/site/jacoco/index.html

# O navegar a:
http://127.0.0.1:{PORT}/target/site/jacoco/index.html
```

### Métricas de Cobertura Actuales
- **Líneas de Código:** > 90%
- **Ramas:** > 85%
- **Métodos:** 100%
- **Clases:** 100%

## Build y Empaquetado

### Compilar
```bash
mvn clean compile
```

### Generar JAR
```bash
mvn clean package
```

El JAR se genera en `target/healthtrack-platform-*.jar`

## CI/CD Pipeline

### GitHub Actions
El proyecto incluye un pipeline automatizado que se ejecuta en:
- Push a ramas `main` y `develop`
- Pull Requests hacia `main`

### Flujo del Pipeline
1. **Setup** - Configuración de Java 17 y cache de Maven
2. **Unit Tests** - Ejecución de tests unitarios
3. **Coverage Report** - Generación de reporte JaCoCo
4. **Performance Tests** - Tests de rendimiento
5. **Functional Tests** - Tests con Selenium
6. **Build** - Compilación y empaquetado
7. **Artifacts** - Subida de reportes y JAR

### Estado del Pipeline
![CI Status](https://github.com/tu-usuario/healthtrack-platform/workflows/HealthTrack%20CI/badge.svg)

## Validación del Bug Original

### Tests de Detección
El proyecto incluye tests específicos que detectan el bug original:

```java
@Test
@DisplayName("Debería detectar el bug original (peso - 1kg)")
void deberiaDetectarBugOriginal() {
    // Given
    Usuario usuario = new Usuario("Test User", 100.0);
    
    // When
    usuario.actualizarPeso(95.0);
    
    // Then - Con la corrección: peso = 95.0, con el bug: peso = 99.0
    assertThat(usuario.getPeso()).isEqualTo(95.0);  // ✅ Correcto
    assertThat(usuario.getPeso()).isNotEqualTo(99.0); // ❌ Bug detectado
}
```

### Casos de Test para el Bug
- Actualización de peso asigna valor correcto
- Múltiples actualizaciones mantienen valores correctos
- Cálculos de diferencia son precisos
- Validación en interfaz web funciona correctamente

## Estructura de Testing

### Tipos de Tests Implementados

| Tipo | Archivo | Propósito |
|------|---------|-----------|
| **Unitarios** | `UsuarioTest.java` | Validación de lógica de negocio |
| **Performance** | `UsuarioPerformanceTest.java` | Evaluación de rendimiento |
| **Funcionales** | `UsuarioFunctionalTest.java` | Simulación de usuario real |
| **Funcionales POM** | `UsuarioFunctionalPOMTest.java` | Tests con Page Object Model |

### Configuración de Tests

```xml
<!-- Profiles en pom.xml -->
mvn test                        # Tests unitarios por defecto
mvn test -Pperformance-tests   # Tests de performance
mvn test -Pfunctional-tests    # Tests funcionales
```

## Contribución

### Proceso de Contribución
1. Fork del repositorio
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Realizar cambios y tests
4. Ejecutar suite completa: `mvn clean verify`
5. Commit con mensaje descriptivo: `git commit -m "feat: add new feature"`
6. Push: `git push origin feature/nueva-funcionalidad`
7. Crear Pull Request

### Estándares de Código
- Tests unitarios para nueva funcionalidad
- Cobertura mínima del 80%
- Tests funcionales para cambios de UI
- Documentación actualizada
- Pipeline CI/CD pasando

### Pull Request Template
El proyecto incluye un template automático para PRs que asegura:
- Descripción clara de cambios
- Validación de tests
- Checklist de calidad

## Métricas del Proyecto

### Estadísticas de Testing
- **Tests Unitarios:** 25+ casos de test
- **Tests de Performance:** 6 benchmarks
- **Tests Funcionales:** 15+ escenarios de usuario
- **Tiempo de Ejecución:** < 5 minutos pipeline completo

### Métricas de Calidad
- **Complejidad Ciclomática:** Baja
- **Duplicación de Código:** < 5%
- **Deuda Técnica:** Mínima
- **Cobertura de Tests:** > 90%

## Resolución de Problemas

### Problemas Comunes

#### Tests Funcionales Fallan
```bash
# Verificar que Chrome esté instalado
google-chrome --version

# Ejecutar con interfaz para debug
mvn test -Pfunctional-tests -Dtest.headless=false
```

#### Error de Coverage
```bash
# Generar reporte sin check
mvn test jacoco:report

# Ver reporte generado
open target/site/jacoco/index.html
```

#### Build Falla
```bash
# Limpiar y recompilar
mvn clean compile

# Debug detallado
mvn clean verify -X
```

## Roadmap

### Próximas Funcionalidades
- [ ] Integración con base de datos
- [ ] API REST para servicios externos
- [ ] Autenticación y autorización
- [ ] Dashboard de métricas de salud
- [ ] Notificaciones automáticas
- [ ] Integración con dispositivos IoT

### Mejoras Técnicas
- [ ] Tests de integración con base de datos
- [ ] Tests de carga con JMeter
- [ ] Análisis de calidad con SonarQube
- [ ] Deploy automático a AWS
- [ ] Monitoreo con métricas de aplicación

## Licencia
Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.
