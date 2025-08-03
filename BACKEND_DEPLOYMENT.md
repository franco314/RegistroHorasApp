# Guía de Despliegue del Backend - TimeTracker Pro

## 🚀 Opciones de Despliegue en la Nube

### 1. **Railway (Recomendado - Gratis)**
- **Ventajas**: Fácil de configurar, gratis para proyectos pequeños
- **URL**: `https://timetracker-pro-api.up.railway.app/api/`
- **Pasos**:
  1. Ve a [railway.app](https://railway.app)
  2. Conecta tu repositorio de GitHub
  3. Sube tu proyecto Django
  4. Configura las variables de entorno
  5. Railway te dará una URL automáticamente

### 2. **Render (Gratis)**
- **Ventajas**: Generoso plan gratuito, fácil configuración
- **URL**: `https://timetracker-pro-api.onrender.com/api/`
- **Pasos**:
  1. Ve a [render.com](https://render.com)
  2. Crea un nuevo Web Service
  3. Conecta tu repositorio
  4. Configura el comando de inicio: `gunicorn proyecto.wsgi:application`

### 3. **Heroku (Pago)**
- **Ventajas**: Muy estable, buena documentación
- **URL**: `https://timetracker-pro-api.herokuapp.com/api/`
- **Pasos**:
  1. Instala Heroku CLI
  2. Crea una nueva app: `heroku create timetracker-pro-api`
  3. Sube tu código: `git push heroku main`

### 4. **PythonAnywhere (Gratis)**
- **Ventajas**: Especializado en Python, muy fácil
- **URL**: `https://tuusuario.pythonanywhere.com/api/`
- **Pasos**:
  1. Ve a [pythonanywhere.com](https://pythonanywhere.com)
  2. Crea una cuenta gratuita
  3. Sube tu proyecto Django
  4. Configura el WSGI

## 📋 Archivos Necesarios para el Backend

### 1. **requirements.txt**
```
Django==4.2.0
djangorestframework==3.14.0
django-cors-headers==4.0.0
gunicorn==20.1.0
whitenoise==6.5.0
psycopg2-binary==2.9.6
python-decouple==3.8
```

### 2. **Procfile** (para Heroku/Railway)
```
web: gunicorn proyecto.wsgi:application --bind 0.0.0.0:$PORT
```

### 3. **runtime.txt** (para Heroku)
```
python-3.11.0
```

### 4. **settings.py** (configuración para producción)
```python
import os
from decouple import config

DEBUG = False
ALLOWED_HOSTS = ['*']  # Configura según tu dominio

# Configuración de base de datos
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': config('DB_NAME'),
        'USER': config('DB_USER'),
        'PASSWORD': config('DB_PASSWORD'),
        'HOST': config('DB_HOST'),
        'PORT': config('DB_PORT', default='5432'),
    }
}

# Configuración de CORS
CORS_ALLOWED_ORIGINS = [
    "https://timetracker-pro.com",
    "http://localhost:3000",
]

CORS_ALLOW_ALL_ORIGINS = True  # Solo para desarrollo
```

## 🔧 Variables de Entorno Necesarias

### Railway/Render/Heroku:
```
SECRET_KEY=tu_clave_secreta_muy_larga
DEBUG=False
DB_NAME=nombre_base_datos
DB_USER=usuario_base_datos
DB_PASSWORD=password_base_datos
DB_HOST=host_base_datos
DB_PORT=5432
```

## 📱 Configuración en Android Studio

### 1. **Cambiar la URL del backend**
En `RetrofitClient.kt`, cambia:
```kotlin
private const val CURRENT_ENVIRONMENT = "PRODUCTION"
```

### 2. **URLs disponibles**:
- `LOCAL`: `http://192.168.0.4:8000/api/`
- `PRODUCTION`: `https://timetracker-pro-api.herokuapp.com/api/`
- `RAILWAY`: `https://timetracker-pro-api.up.railway.app/api/`
- `RENDER`: `https://timetracker-pro-api.onrender.com/api/`

## 🎯 Pasos para Desplegar

### Paso 1: Preparar el Backend
1. Asegúrate de que tu proyecto Django tenga:
   - `requirements.txt`
   - `Procfile`
   - Configuración de CORS
   - Variables de entorno

### Paso 2: Elegir Plataforma
1. **Railway** (más fácil):
   - Ve a railway.app
   - Conecta tu repositorio
   - Configura variables de entorno
   - ¡Listo!

### Paso 3: Actualizar Android Studio
1. Cambia `CURRENT_ENVIRONMENT` a `"PRODUCTION"`
2. Compila y prueba la app
3. La app ahora funcionará desde cualquier lugar

## 🔍 Verificación

### 1. **Probar la API**:
```bash
curl https://tu-api.railway.app/api/registros/
```

### 2. **Verificar en la app**:
- Abre la app
- Intenta hacer login
- Verifica que los registros se guarden

## 🚨 Solución de Problemas

### Error de CORS:
```python
# En settings.py
CORS_ALLOW_ALL_ORIGINS = True
CORS_ALLOW_CREDENTIALS = True
```

### Error de base de datos:
- Verifica las variables de entorno
- Asegúrate de que la base de datos esté creada

### Error de conexión en Android:
- Verifica que la URL sea correcta
- Asegúrate de que el servidor esté funcionando

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del servidor
2. Verifica las variables de entorno
3. Prueba la API con Postman
4. Revisa la configuración de CORS

---

*Una vez desplegado, tu app funcionará desde cualquier lugar sin depender de tu PC* 