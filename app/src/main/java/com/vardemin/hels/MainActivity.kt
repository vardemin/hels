package com.vardemin.hels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vardemin.hels.app.R
import com.vardemin.hels.ui.theme.HttpServerTheme
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SseEvent(val data: String, val event: String? = null, val id: String? = null)

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<SseEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        for (event in events) {
            if (event.id != null) {
                write("id: ${event.id}\n")
            }
            if (event.event != null) {
                write("event: ${event.event}\n")
            }
            for (dataLine in event.data.lines()) {
                write("data: $dataLine\n")
            }
            write("\n")
            flush()
        }
    }
}

class MainActivity : ComponentActivity() {

    private val server by lazy {
        embeddedServer(CIO, port = 1515) {
            val channel = produce { // this: ProducerScope<SseEvent> ->
                var n = 0
                while (true) {
                    send(SseEvent("demo $n "))
                    delay(1000)
                    n++
                }
            }.broadcast()
            routing {
                get("/") {
                    call.respondBytes(contentType = ContentType.Text.Html) {
                        this@MainActivity.resources.openRawResource(R.raw.index).readBytes()
                    }
                }
                get("/bulma.css") {
                    call.respondBytes(contentType = ContentType.Text.CSS) {
                        this@MainActivity.resources.openRawResource(R.raw.bulma).readBytes()
                    }
                }
                get("/sse") {
                    val events = channel.openSubscription()
                    try {
                        call.respondSse(events)
                    } finally {
                        events.cancel()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HttpServerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            server.start(wait = true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop(1_000, 2_000)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HttpServerTheme {
        Greeting("Android")
    }
}
