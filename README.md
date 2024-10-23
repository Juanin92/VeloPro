<img src="src/main/resources/Images/principalLogo.png" alt="Logo de VeloPro" width="50"/>

# VeloPro - Sistema de Gestión de Ventas e Inventario

**VeLoPro** es un sistema de gestión de ventas e inventario diseñado para pequeños comercios que operan sin acceso a internet. La aplicación es completamente local y facilita la administración de ventas, inventario, clientes y reportes de manera eficiente.
<hr>

## Características Principales

- **Roles de Usuario**: El sistema cuenta con cinco roles predefinidos, cada uno con accesos y permisos específicos:
    - **Master**: Tiene acceso completo a todas las funcionalidades del sistema.
    - **Administrador**: Control total, incluyendo la modificación de roles.
    - **Inventarista**: Acceso solo a la gestión de inventario, sin permisos para realizar ventas ni gestionar abonos de clientes.
    - **Vendedor**: Acceso a la venta y gestión de inventario, pero limitado en la creación de clientes y compras.
    - **Invitado**: Acceso limitado, con permisos muy restringidos.

- **Login y Recuperación de Contraseña**: El usuario inicia sesión mediante una pantalla de login. Si olvida su contraseña, puede solicitar el envío de la misma al correo registrado.

- **Gestión de Caja**: Al iniciar sesión, se solicita al usuario ingresar los valores de apertura de caja (excepto al rol Inventarista). Al salir del sistema, se registra el cierre de caja y el cierre de terminal POS.

- **Gestión de Ventas**: En la sección de ventas, el usuario puede seleccionar productos y métodos de pago como efectivo, débito, crédito, transferencia, préstamo (se requiere seleccionar un cliente) o pago mixto. Al finalizar la venta, se ofrece la opción de imprimir la boleta.

- **Inventario**: Permite visualizar, actualizar y eliminar productos. Se pueden agregar nuevos productos, crear categorías y subcategorías, gestionar proveedores y registrar compras de productos.

- **Clientes**: Gestión de clientes con detalles, saldo de deudas y la posibilidad de abonar a cuentas pendientes.

- **Reportes y Kardex**: Se generan gráficos y tablas con datos de ventas y movimientos de productos. Además, es posible exportar reportes en formato Excel.

- **Historial de Actividades**: El rol Master tiene acceso a un registro completo de todas las acciones realizadas por los usuarios en el sistema.

- **Configuración de Usuarios**: Solo los roles Master y Administrador pueden crear nuevos usuarios y gestionar las cuentas existentes.
<hr>

## Tecnologías Utilizadas

- **Lenguaje**: Java
- **Frameworks**: Spring Boot, JavaFX
- **Base de Datos**: MySQL
- **Interfaz de Usuario**: CSS y JavaFX (con SceneBuilder)
- **Bibliotecas**:
    - Apache PDFBox (para generación de PDFs)
    - Apache POI (para manejo de archivos Excel)
<hr>

## Requisitos de Instalación

Para ejecutar **VeloPro**, asegúrate de tener instaladas las siguientes herramientas en tu equipo:

1. **Java JDK 21 o superior**.
2. **MySQL** para la gestión de base de datos.
3. **Maven** para gestionar las dependencias del proyecto.
4. **SceneBuilder** para editar las interfaces gráficas, si es necesario.
<hr>

### Configuración de MySQL

1. Crea una base de datos en MySQL con el siguiente script:
    ```sql
    CREATE DATABASE velopro;
    ```
2. Actualiza los valores de conexión en el archivo `application.properties` en el proyecto para conectar correctamente tu base de datos.
<hr>

### Ejecución del Proyecto

1. Clona este repositorio:
    ```bash
    git clone https://github.com/tuusuario/VeloPro.git
    ```
2. Configura tu entorno de desarrollo y ejecuta el siguiente comando desde la raíz del proyecto:
    ```bash
    mvn spring-boot:run
    ```

El programa iniciará en la vista de login, y el primer usuario por defecto será el "Master" con acceso completo. El username: "ADMIN", contraseña: "ADMINxxxx". Debes cambiar los valores X será el año actual que se ejecutó el programa.  
<hr>

## Contribuciones

Si deseas contribuir a este proyecto, sigue los siguientes pasos:

1. Haz un fork de este repositorio.
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza los cambios y haz un commit (`git commit -am 'Agregar nueva funcionalidad'`).
4. Haz push de la rama (`git push origin feature/nueva-funcionalidad`).
5. Envía un pull request.
<hr>

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.