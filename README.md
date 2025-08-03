# TimeTracker Pro

Aplicación Android desarrollada en **Kotlin** que permite a trabajadores registrar sus horas diarias, consultar registros anteriores y visualizar un resumen semanal y mensual de horas trabajadas y horas extra.

## 📄 Descripción

- Los usuarios pueden cargar la fecha y la cantidad de horas trabajadas.
- La app calcula automáticamente el total de horas semanales y mensuales, y muestra las horas extra si se superan ciertos límites.
- Incluye una sección para ver los registros guardados y un resumen acumulado actualizado en cualquier momento.
- **Nuevo diseño moderno** con logo profesional que transmite eficiencia y acumulación de tiempo.

## ⚙️ Tecnologías utilizadas

- **Kotlin** (Android)
- **Python** y **Django** para el backend (API REST)
- **Postman** para pruebas y documentación de endpoints
- **Retrofit** para la comunicación entre app y servidor
- **Material Design** para interfaz moderna y profesional

## ✅ Funcionalidades destacadas

- Login con token para autenticación segura.
- Configuración de día de franco semanal.
- Visualización en pantalla de resumen de horas acumuladas.
- **Interfaz moderna** con diseño profesional y eficiente.
- **Logo adaptativo** que representa acumulación de tiempo y eficiencia.

## 🎨 Nuevo Diseño

- **Logo profesional** con elementos que representan tiempo y eficiencia
- **Paleta de colores moderna**: Azul profesional, verde para crecimiento, naranja para energía
- **Iconos vectoriales** optimizados para diferentes tamaños de pantalla
- **Tema Material Design** actualizado con colores profesionales

## 🚀 Despliegue en la Nube

La app está configurada para funcionar con un backend en la nube, permitiendo que funcione desde cualquier lugar sin depender de tu PC.

### Configuración Actual:
- **Entorno**: Configurado para producción
- **Timeouts**: Optimizados para conexiones remotas
- **Manejo de errores**: Mejorado para conexiones de internet

### Para desplegar el backend:
1. Ve a `BACKEND_DEPLOYMENT.md` para instrucciones detalladas
2. Elige una plataforma (Railway recomendado)
3. Sube tu proyecto Django
4. Cambia la URL en `RetrofitClient.kt` si es necesario

### Plataformas recomendadas:
- **Railway** (Gratis, fácil)
- **Render** (Gratis, generoso)
- **Heroku** (Pago, estable)
- **PythonAnywhere** (Gratis, especializado en Python)
