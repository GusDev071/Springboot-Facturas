# 🧾 Sistema de Facturación con Inyección de Dependencias (Spring Boot)

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/Architecture-REST%20API-blue?style=for-the-badge" alt="REST API" />
</p>

---

## 📌 Descripción del Proyecto

**springboot-difactura** es una aplicación backend desarrollada con **Spring Boot** y **Java 17** orientada a ilustrar los fundamentos avanzados del contenedor IoC (Inversión de Control) e Inyección de Dependencias (**DI**) en el ecosistema Spring.

El proyecto modela un sistema de emisión de facturas (`Invoice`) con clientes (`Client`), líneas de detalle (`Item`) y productos (`Product`), demostrando el uso de:
- Inyección de dependencias mediante `@Autowired`.
- Configuración explícita con clases `@Configuration` y métodos `@Bean`.
- Desambiguación de componentes a través de `@Qualifier`.
- Externalización de configuración con `@PropertySource` y `@Value`.
- Manejo del ciclo de vida de los beans con `@PostConstruct` y `@PreDestroy`.
- Alcance o ámbito de bean web mediante `@RequestScope`.

---

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Conceptos de Spring Aplicados](#-conceptos-de-spring-aplicados)
- [Estructura del Repositorio](#-estructura-del-repositorio)
- [API Endpoints y Ejemplos](#-api-endpoints-y-ejemplos)
- [Configuración de Propiedades](#-configuración-de-propiedades)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [Autor](#-autor)

---

## 🚀 Características Principales

- **Gestión de Componentes IoC:** Declaración desacoplada y modular de servicios y modelos.
- **Beans Dinámicos por Petición (`@RequestScope`):** Cada petición HTTP recibe una instancia aislada de factura y cliente, garantizando la concurrencia segura y el reinicio de estado.
- **Configuración Modular de Catálogos:** Capacidad de intercambiar listas de productos (catálogo general vs. catálogo de oficina) mediante calificadores de Spring.
- **Cálculo Automático de Importes:** Uso eficiente de Java Streams para el cálculo reactivo del total de la factura.
- **Endpoints RESTful:** Exposición de resultados en formato JSON estructurado.

---

## 🏗 Arquitectura del Proyecto

```mermaid
graph TD
    ClientReq([Cliente HTTP / Navegador]) -->|GET /invoices/show| Controller[InvoiceController]
    Controller -->|Inyecta| InvoiceBean["Invoice (@RequestScope)"]
    InvoiceBean -->|Inyecta| ClientBean["Client (@RequestScope)"]
    InvoiceBean -->|@Qualifier('default')| AppConf["appConfig (@Configuration)"]
    AppConf -->|Carga| PropSource["data.properties (@PropertySource)"]
    AppConf -->|Provee| ItemsBean["List&lt;Item&gt; (itemsInvoice)"]
    ItemsBean --> ItemModel["Items & Products"]
    
    subgraph Ciclo de Vida del Bean
        Init["@PostConstruct: init()"] --> ExecTotal["getTotal(): Java Streams"] --> Destroy["@PreDestroy: destroy()"]
    end
```

---

## 💡 Conceptos de Spring Aplicados

### 1. Inyección de Dependencias y Desambiguación
En `appConfig.java` se configuran múltiples listas de ítems (`itemsInvoice` e `itemsInvoiceOffice`). En `Invoice.java`, se especifica exactamente qué catálogo inyectar mediante:
```java
@Autowired
@Qualifier("default")
private List<Item> items;
```

### 2. Alcance por Petición HTTP (`@RequestScope`)
Tanto `Invoice` como `Client` utilizan la anotación `@RequestScope`:
```java
@Component
@RequestScope
public class Invoice { ... }
```
Esto asegura que los beans se creen al iniciar una petición web y se destruyan al finalizarla, evitando la colisión de datos entre múltiples clientes concurrentes.

### 3. Métodos del Ciclo de Vida (`@PostConstruct` y `@PreDestroy`)
- **`@PostConstruct`:** Modifica dinámicamente los datos de la factura tras la inyección de atributos y dependencias.
- **`@PreDestroy`:** Se ejecuta al cerrarse el ciclo de vida de la petición HTTP, notificando la liberación del bean.

### 4. Externalización de Configuración
Lectura de archivos de propiedades con soporte para caracteres especiales UTF-8:
```java
@Configuration
@PropertySource(value = "classpath:data.properties", encoding = "UTF-8")
public class appConfig { ... }
```
E inyección de propiedades en clases mediante `@Value`:
```java
@Value("${client.name}")
private String name;
```

### 5. Cálculo con Java Stream API
El cálculo del total acumulado se realiza de manera declarativa con Streams:
```java
public int getTotal() {
    return items.stream()
            .map(item -> item.getImporte())
            .reduce(0, (sum, importe) -> sum + importe);
}
```

---

## 📂 Estructura del Repositorio

```text
springboot-difactura/
├── src/
│   ├── main/
│   │   ├── java/com/gustavo/curso/springboot/di/factura/springboot_difactura/
│   │   │   ├── SpringbootDifacturaApplication.java   # Clase principal (Spring Boot Starter)
│   │   │   ├── appConfig.java                        # Configuración de Beans e inyección de datos
│   │   │   ├── controllers/
│   │   │   │   └── InvoiceController.java           # Controlador REST de Facturas
│   │   │   └── models/
│   │   │       ├── Client.java                       # Modelo y Bean de Cliente (@RequestScope)
│   │   │       ├── Invoice.java                      # Modelo principal de Factura (@RequestScope)
│   │   │       ├── Item.java                         # Línea de detalle (producto y cantidad)
│   │   │       └── Product.java                      # Entidad Producto (nombre y precio)
│   │   └── resources/
│   │       ├── application.properties                # Configuración de la aplicación Spring
│   │       └── data.properties                       # Datos parametrizados de clientes y facturas
│   └── test/                                         # Pruebas unitarias y de integración
├── pom.xml                                           # Gestión de dependencias con Maven
├── mvnw / mvnw.cmd                                   # Maven Wrapper (Linux / Windows)
└── README.md                                         # Documentación del proyecto
```

---

## 📡 API Endpoints y Ejemplos

### Obtener Detalle de la Factura

- **Ruta:** `/invoices/show`
- **Método HTTP:** `GET`
- **Descripción:** Genera y retorna la factura completa con los datos del cliente, descripción y desglose de ítems calculados.

#### Solicitud (cURL)
```bash
curl -X GET http://localhost:8080/invoices/show
```

#### Respuesta Ejemplo (`200 OK`)
```json
{
  "client": {
    "name": "Gustavo Pepe",
    "lastname": "Flores"
  },
  "description": "Factura deporte del cliente: Gustavo Pepe Flores",
  "items": [
    {
      "product": {
        "name": "Camara sony",
        "price": 800
      },
      "quantity": 2,
      "importe": 1600
    },
    {
      "product": {
        "name": "Camara samsung",
        "price": 900
      },
      "quantity": 4,
      "importe": 3600
    },
    {
      "product": {
        "name": "Camara nokia",
        "price": 1000
      },
      "quantity": 6,
      "importe": 6000
    }
  ],
  "total": 11200
}
```

---

## ⚙ Configuración de Propiedades

El archivo `src/main/resources/data.properties` contiene la información base inyectada en la factura:

```properties
client.name=Gustavo
client.lastname=Flores
invoice.description=Factura deporte
invoice.description.office=Factura de oficina
```

> **Tip:** Puedes modificar estos valores o cambiar el `@Qualifier` en `Invoice.java` a `"itemsInvoiceOffice"` para alternar fácilmente entre facturas comerciales y facturas de suministros de oficina.

---

## 🛠 Requisitos Previos

Asegúrate de contar con las siguientes herramientas instaladas:
- **Java Development Kit (JDK):** Versión 17 o superior.
- **Maven:** Versión 3.9+ (Opcional si utilizas el Maven Wrapper integrado `mvnw`).
- **Git** (para clonar y versionar el repositorio).

---

## 💻 Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/GusDev071/Springboot-Facturas.git
   cd springboot-difactura
   ```

2. **Compilar el proyecto:**
   - En Windows:
     ```powershell
     .\mvnw.cmd clean compile
     ```
   - En macOS / Linux:
     ```bash
     ./mvnw clean compile
     ```

3. **Iniciar el servidor de desarrollo:**
   - En Windows:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - En macOS / Linux:
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Acceder a la aplicación:**
   Abre tu navegador o cliente REST (Postman, Insomnia, Thunder Client) e ingresa a:
   ```text
   http://localhost:8080/invoices/show
   ```

---

## 👨‍💻 Autor

- **Gustavo Flores** - [@GusDev071](https://github.com/GusDev071)
