<p align="center">
  <img src="./src/main/resources/Images/principalLogo.png" width="120"/>
</p>

# VeloPro Desktop

![Java](https://img.shields.io/badge/Backend-Java%2021-orange)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203-brightgreen)
![JavaFX](https://img.shields.io/badge/UI-JavaFX-blue)
![Architecture](https://img.shields.io/badge/Architecture-Layered-blue)
![Type](https://img.shields.io/badge/System-Desktop%20App%20%7C%20ERP--like-purple)
![Database](https://img.shields.io/badge/Database-MySQL-blue)

**VeloPro Desktop** es una aplicación de escritorio tipo ERP orientada a la gestión de ventas e inventario en entornos sin conexión a internet.

Diseñada para ofrecer control total de las operaciones comerciales de forma local, asegurando continuidad operativa, trazabilidad de datos y eficiencia en la gestión diaria.

---

## 📌 Descripción

VeloPro Desktop centraliza procesos críticos de un negocio en una sola aplicación, permitiendo administrar ventas, inventario, clientes y reportes sin depender de conexión a internet.

El sistema reduce errores operativos, mejora la organización de la información y permite mantener un control completo del negocio desde un único punto.

---

## ⚙️ Características Principales

* **Sistema de Punto de Venta (POS interno):** Registro de ventas con múltiples métodos de pago (efectivo, débito, crédito, transferencia, préstamo y pago mixto).
* **Gestión de Caja:** Apertura y cierre de caja por sesión de usuario.
* **Gestión de Inventario:** Control de productos, categorías, proveedores y compras.
* **Gestión de Clientes:** Registro de clientes, control de deudas y abonos.
* **Reportes y Kardex:** Visualización de datos y exportación a Excel.
* **Historial de Transacciones:** Auditoría completa de operaciones del sistema.
* **Control de Usuarios:** Gestión de roles y permisos personalizados.

---

## 🛠️ Stack Técnico

### Backend / Core

* Java 21
* Spring Boot 3
* Spring Data JPA / JDBC
* MySQL
* Lombok

### Interfaz de Usuario

* JavaFX
* CSS
* SceneBuilder

### Librerías

* Apache PDFBox (generación de PDFs)
* Apache POI (exportación a Excel)

---

## 🏗️ Arquitectura del Proyecto

El sistema sigue una arquitectura en capas (Layered Architecture), adaptada a una aplicación de escritorio con manejo de estado local y sesiones activas:

1. **Controller Layer:** Manejo de eventos e interacción con la UI (JavaFX)
2. **Service Layer:** Lógica de negocio y procesamiento de operaciones
3. **Repository Layer:** Acceso y persistencia de datos
4. **Model Layer:** DTOs, entidades y enums del sistema

Módulos complementarios:

* **Security:** Gestión de autenticación y control de acceso
* **Session:** Manejo de sesión activa del usuario
* **Validation:** Validación de datos y reglas de entrada
* **Utility:** Funciones auxiliares reutilizables

Aplicando buenas prácticas de:

* Clean Code  
* Principios SOLID  
* Separación de responsabilidades  

---

## 📁 Estructura del Proyecto

```bash
## 📁 
veloPro/
│
├── VeloPro/
│   ├── controller/
│   ├── model/
│   │   ├── dto/
│   │   ├── entity/
│   │   └── enums/
│   ├── repository/
│   ├── service/
│   ├── security/
│   ├── session/
│   ├── utility/
│   └── validation/
│
├── resources/
│   ├── CSS/
│   ├── images/
│   ├── PDFTemplate/
│   └── view/
```

---

## 🔐 Seguridad

* Autenticación mediante login
* Recuperación de contraseña vía correo
* Control de acceso basado en roles
* Validaciones en operaciones críticas:

  * Apertura y cierre de caja
  * Modificación de datos sensibles
  * Acciones restringidas según rol

---

## 📸 Capturas del Sistema

![Demo](./src/main/resources/Images/VeLoPro.gif)

---

## ⚡ Beneficios del Sistema

* Operación completamente offline
* Reducción de errores manuales
* Control centralizado del negocio
* Mejora en la trazabilidad de operaciones
* Adaptabilidad a pequeños comercios

---

## 🛠️ Instalación y Ejecución

### Requisitos

* Java 21
* MySQL
* Maven

---

### Configuración de Base de Datos

```sql
CREATE DATABASE velopro;
```

Configurar credenciales en:

```
application.properties
```

---

### Ejecución

```bash
git clone https://github.com/tuusuario/VeloPro.git
cd VeloPro
mvn spring-boot:run
```

---

### Credenciales Iniciales

* Usuario: `ADMIN`
* Contraseña: `ADMINxxxx` (xxxx = año actual)

⚠️ Se recomienda cambiar las credenciales en el primer uso.

---

## 📌 Estado del Proyecto

Proyecto funcional desarrollado como solución real para gestión de negocios en entornos offline.

---

## 👨‍💻 Autor

**Juan Ignacio Clavería** - *Full Stack Developer*

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge\&logo=linkedin\&logoColor=white)](https://www.linkedin.com/in/juan-ignacio-claver%C3%ADa/)
[![Portfolio](https://img.shields.io/badge/Portfolio-222222?style=for-the-badge\&logo=github\&logoColor=white)](https://juanin92.github.io/)

---

> 💡 **Nota para Reclutadores:**
> Este proyecto demuestra experiencia en desarrollo de aplicaciones de escritorio con Java, diseño de sistemas offline, implementación de arquitectura en capas y construcción de soluciones orientadas a negocio con múltiples módulos integrados.
