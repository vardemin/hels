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

## Integration
### Include Dependency
HELS is distributed through the maventCentral. There are 2 library versions: hels-full and hels-release.
Full version encapsulates all the features, but lightweight release is only contains empty implementations.
Useful for debug and release build variants that could be included as follows:
```groovy
dependencies {
    debugImplementation "io.github.vardemin:hels-full:$helsVersion"
    releaseImplementation "io.github.vardemin:hels-release:$helsVersion"
}
```
### Initialize Library
HELS Initialization could be invoked in async, suspend or blocking modes.
You could send longs without need to await initialization anyway. init() method accepts context and
initial session attributes as parameters.
```kotlin
class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        HelsInitializer.initAsync(this, mapOf(
            "version" to BuildConfig.VERSION_NAME,
            "version_code" to BuildConfig.VERSION_CODE
        ))
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
```kotlin
OkHttpClient.Builder()
    // Some other interceptors
    .addInterceptor(HelsInterceptor())
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
![Screenshot of events screen](/screenshots/screen3.png)

## Example App
Located in 'app' module.