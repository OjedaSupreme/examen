# Guía Completa: Cómo Probar SSL/TLS en un Microservicio con Java y Spring Boot

Esta guía detalla el proceso paso a paso para configurar, implementar y probar conexiones seguras (HTTPS/TLS) en un microservicio desarrollado con Spring Boot.

---

## 1. Generar un Certificado Autofirmado (Para Desarrollo/Pruebas)

Para probar HTTPS localmente, necesitas un almacén de claves (*keystore*). Java incluye la herramienta `keytool` para generarlo fácilmente.

Ejecuta el siguiente comando en tu terminal para crear un certificado autofirmado en formato PKCS12:

```bash
keytool -genkeypair -alias mymicroservice -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -dname "CN=localhost, OU=Development, O=MyCompany, L=City, ST=State, C=MX"
```

* **Contraseña:** Recuerda la contraseña que asignes (por ejemplo, `changeit`).
* **Ubicación:** Coloca el archivo resultante `keystore.p12` dentro de la carpeta `src/main/resources/` de tu proyecto.

---

## 2. Configurar Spring Boot para Habilitar HTTPS

Abre tu archivo `application.properties` (o `application.yml`) y añade las siguientes propiedades:

```properties
# Puerto seguro en el que correrá la aplicación
server.port=8443

# Habilitar SSL
server.ssl.enabled=true

# Configuración del Keystore
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=changeit
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=mymicroservice
```

---

## 3. Métodos para Probar SSL/TLS

Una vez que arranques tu aplicación Spring Boot, puedes verificar el funcionamiento de las siguientes formas:

### A. Usando el Navegador Web
Accede a:
> `https://localhost:8443/tu-endpoint`

* *Nota:* Como es un certificado autofirmado, el navegador mostrará una advertencia de seguridad. Haz clic en **Avanzados** y luego en **Continuar hacia localhost (inseguro)**.

### B. Usando cURL en la Terminal
El parámetro `-k` (o `--insecure`) le indica a cURL que ignore la validación del certificado autofirmado:

```bash
curl -k https://localhost:8443/tu-endpoint
```

Para ver detalles técnicos del handshake TLS y los cifrados:

```bash
curl -vk https://localhost:8443/tu-endpoint
```

### C. Usando Postman
1. Asegúrate de que la URL empiece con **`https://`** (ej. `https://localhost:8443/tu-endpoint`). Si usas `http://`, obtendrás el error: `Bad Request: This combination of host and port requires TLS`.
2. Si Postman bloquea la petición por el certificado autofirmado, ve a **Settings** (rueda dentada) > **Settings** > **General** y cambia **SSL certificate verification** a **OFF**.

---

## 4. Solución de Problemas Comunes

### Error: `Unable to create key store: Could not load store from 'classpath:keystore.p12'`
* **Causa:** Spring Boot no encuentra el archivo en el classpath.
* **Solución:** Asegúrate de que `keystore.p12` esté exactamente en `src/main/resources/`. Si persiste, puedes usar una ruta absoluta en la propiedad:
  ```properties
  server.ssl.key-store=file:/ruta/absoluta/a/tu/keystore.p12
  ```

### Error de compilación al intentar forzar redirección HTTP a HTTPS con Tomcat
Si por alguna razón intentas usar clases internas de Tomcat (`ServletWebServerFactory`, `TomcatServletWebServerFactory`) y te enfrentas a problemas de resolución de paquetes o dependencias complejas, **recuerda que no necesitas esto para probar SSL/TLS**. 

Para la gran mayoría de escenarios de prueba y desarrollo local, basta con utilizar las propiedades estándar de `application.properties` listadas en el **Paso 2**.


**SSL** y **TLS** son protocolos de seguridad diseñados para cifrar la comunicación que viaja a través de una red (como internet), garantizando que los datos enviados entre un cliente (por ejemplo, tu navegador o Postman) y un servidor (tu microservicio) viajen de forma segura y privada.

---

### 1. ¿Qué es SSL? (*Secure Sockets Layer*)

* **Origen:** Fue el **pionero**. Fue desarrollado por la empresa Netscape a mediados de la década de 1990 para proteger las transacciones de comercio electrónico.
* **Estado actual:** **Está completamente obsoleto y descontinuado**. Se descubrieron múltiples vulnerabilidades de seguridad críticas a lo largo de los años (como los ataques POODLE o HEARTBLEED en variantes relacionadas), por lo que ningún servidor moderno ni navegador actual lo permite.
* **El término comercial:** Aunque técnicamente ya no existe, la gente y las empresas siguen utilizando la palabra "SSL" por costumbre (por ejemplo, cuando dicen "comprar un certificado SSL"). Hoy en día, cuando escuchas "SSL", en realidad te están hablando de TLS.

### 2. ¿Qué es TLS? (*Transport Layer Security*)

* **Origen:** Es el **sucesor oficial y moderno de SSL**. Tomó las bases de SSL pero fue rediseñado desde cero por la organización IETF (Internet Engineering Task Force) para corregir todos los agujeros de seguridad de su antecesor.
* **Estado actual:** Es el **estándar actual y obligatorio de internet**. Las versiones más seguras y utilizadas hoy en día son **TLS 1.2** y **TLS 1.3** (esta última mucho más rápida y segura).

---

### ¿Cómo funcionan en la práctica? (El "Handshake" o Apresto de Manos)

Cuando configuras tu microservicio en Spring Boot con un certificado y te conectas mediante `https://`, ocurre un proceso invisible en milisegundos llamado **Handshake TLS**:

1. **Hola (Client Hello):** Tu cliente (Postman o navegador) saluda al servidor y le dice: *"Hola, quiero hablar contigo de forma segura y estas son las versiones de TLS y cifrados que entiendo"*.
2. **Respuesta y Certificado (Server Hello):** Tu microservicio responde: *"Hola, perfecto. Usemos TLS 1.3 y aquí tienes mi **Certificado Digital** para que verifiques que soy quien digo ser"*.
3. **Verificación:** El cliente valida el certificado (por eso los autofirmados como el de tu prueba local generan una alerta, porque no son emitidos por una entidad confiable reconocida).
4. **Acuerdo de llaves (Key Exchange):** Ambos lados acuerdan de forma secreta una "llave de cifrado" temporal única para esa sesión.
5. **Cifrado total:** A partir de ese momento, cualquier dato que viaje (tu usuario, contraseñas, tokens JSON, etc.) va completamente **cifrado**. Si alguien intercepta la red en el medio, solo verá caracteres basura imposibles de leer.

### En resumen:

* **SSL** es el abuelo inseguro que ya no se usa.
* **TLS** es el estándar moderno, robusto y seguro que protege todo el internet actual (por eso los sitios usan `https://`). Cuando configuras tu `keystore.p12` en Spring Boot, lo que estás haciendo es habilitar el protocolo **TLS** en tu servidor.