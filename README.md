# HELS
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.vardemin/hels-full)
*Embedded Logging Server*. Tool for realtime Android application debugging in browser on any device
within local network.

### HELS Android library features
- Logging simple messages or errors with tags in usual android Log or Timber style
- Logging events with attributes
- Logging network requests or use OkHttp HelsInterceptor
- Stores logs on device for two consecutive sessions
- Configuring local server port number and implement custom DataSources
- Possibility of overriding on-board frontend archive

### HELS Frontend features
- View realtime logs, events and requests data in tables
- Data sorting, filtering by different fields
- Fetch data with pagination
- Switch Dark/Light mode
- Copy data rows
- View request details
- Multilingual support (en, es, de, jp, ru)

## Integration
### Include Dependency
HELS is distributed through the maventCentral. There are 2 library versions: hels-full and hels-release.
Full version encapsulates all the features (except HelsInterceptor), but lightweight release
contains only empty implementations.
Useful for debug and release build variants that could be included as follows:
```groovy
dependencies {
    debugImplementation "io.github.vardemin:hels-full:$helsVersion"
    releaseImplementation "io.github.vardemin:hels-release:$helsVersion"
    implementation "io.github.vardemin:hels-network:$helsVersion" // For OkHttp3 Interceptor
}
```
### androidx.startup Initialization
HELS implements androidx.startup Initializer where initial config provides through the inheriting
HelsConfigurationProvider interface in your Application class.
```kotlin
interface HelsConfigurationProvider {
    fun getHelsConfiguration(): HelsConfiguration
}
class HelsConfiguration(
    val startNewSession: Boolean = true,
    val port: Int = HELS_DEFAULT_PORT, // 1515
    val initialProperties: Map<String, String> = emptyMap(),
    val customFrontendVersion: Int? = null // if custom frontend overridden
)
```
### Manual Initialization
Manual Initialziation available after disabling HelsStartupInitializer.
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data android:name="com.vardemin.hels.initializer.HelsStartupInitializer"
              tools:node="remove" />
</provider>
```
HELS Initialization could be invoked manually in async, suspend or blocking modes.
You could send longs without need to await initialization anyway. init() method accepts context and
initial session attributes as parameters.
```kotlin
class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        HelsInitializer.initAsync(this, HelsConfiguration(initialProperties = mapOf(
            "version" to BuildConfig.VERSION_NAME,
            "version_code" to BuildConfig.VERSION_CODE
        )))
    }
}
```
### Log messages and errors
```kotlin
Hels.d(tag, message)
Hels.i(tag, message)
Hels.e(tag, message)
Hels.e(tag, throwable)
```
### Log events
Each event consist of title, message and properties Map<String, String>
```kotlin
Hels.event(title, message, mapOf(
    "time" to dateTimeStr,
    "userId" to "4535342",
    "code" to "123"
))
```
### Log requests
**Manual logging**
There are 3 available methods:
- logRequest(): String that logs started request and returns unique id
- logResponse() that logs response by request id
- logFullRequest() for logging finished request
```kotlin
val requestId = Hels.logRequest(
    request.method,
    request.url.toString(),
    headers.toMap(),
    maxOf(totalRequestBodySize, 0L),
    bodyString,
    currentDateTime()
)
Hels.logResponse(
    requestId,
    response.code,
    responseHeaders.toMap(),
    maxOf(totalResponseBodySize, 0L),
    responseString,
    endTime
)
```
**Adding OkHttp Interceptor**
Just include HelsInterceptor where maxBodySize - max bytes lenght of bodies to log. By default 1024.
'0' to disable body size filter.
```kotlin
OkHttpClient.Builder()
    // Some other interceptors
    .addInterceptor(
        HelsInterceptor(
            Hels, // Hels central object implements HelsNetworkLogger
            BuildConfig.DEBUG, // enable only in Debug mode
            maxBodySize // max body size in bytes to capture in body: String fields
        )
    )
    .build()
```

## Frontend
Available on http://{testingDeviceIp}:1515 address. Port could be configured on initialization stage.
### Starting configuration page
By default you have to confirm and save current configuration. You will view short
information about device and session, if all works ok.
![Screenshot of configuration screen](/screenshots/screen1.png)
### Logs screen
![Screenshot of logs screen](/screenshots/screen2.png)
### Events screen
![Screenshot of events screen](/screenshots/screen3.png)
### Requests screen
![Screenshot of events screen](/screenshots/screen4.png)

## Frontend override
1. Place front.zip in raw resources folder
2. Override customFrontendVersion in HelsConfiguration()
3. Each frontend update should increment customFrontendVersion parameter

## REST API
All functionality available through the REST API and websockets (for updates)
### Sessions API
GET /api/v1/sessions - get all recorded sessions (SessionItem[])
GET /api/v1/session - get current session (SessionItem)
WS /ws/session - subscribe to session changes (SessionItem as json)
### Logs API
GET /api/v1/logs - get all recorded logs (LogItem[])
GET /api/v1/logs/{id} - get log by id (RequestItem)
WS /ws/logs - subscribe to logs operations (HelsOperation as json)
### Events API
GET /api/v1/events - get all recorded events (EventItem[])
GET /api/v1/events/{id} - get event by id (RequestItem)
WS /ws/events - subscribe to logs operations (HelsOperation as json)
### Requests API
GET /api/v1/requests - get all recorded requests (light RequestItem[] list without bodies)
GET /api/v1/requests/{id} - get request by id (RequestItem)
WS /ws/requests - subscribe to logs operations (HelsOperation as json)

## Example App
Located in 'app' module.
