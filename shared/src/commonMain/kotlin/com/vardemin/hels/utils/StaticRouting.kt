package com.vardemin.hels.utils

import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.content.LastModifiedVersion
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.versions
import io.ktor.http.fromFileExtension
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.charsets.Charsets
import okio.*

expect val fileSystem: FileSystem

fun Application.configureStaticRouting(path: String, rootFolder: Path) {
    routing {
        route(path) {
            staticRootFolder = rootFolder
            fileSystem.listRecursively(rootFolder).filter { path ->
                fileSystem.metadata(path).isRegularFile
            }.forEach { path ->
                val relativePath = path.relativeTo(rootFolder).toString()
                file(relativePath, relativePath)
            }
            default("index.html")
        }
    }
}

fun Route.default(localPath: String) {
    val path = staticRootFolder?.resolve(localPath) ?: return
    get {
        call.respondStatic(path)
    }
}

private val staticRootFolderKey = AttributeKey<Path>("BaseFolder")

var Route.staticRootFolder: Path?
    get() = attributes.getOrNull(staticRootFolderKey) ?: parent?.staticRootFolder
    set(value) {
        if (value != null) {
            attributes.put(staticRootFolderKey, value)
        } else {
            attributes.remove(staticRootFolderKey)
        }
    }

fun Route.file(remotePath: String, localPath: String) {
    val path = staticRootFolder?.resolve(localPath) ?: return

    get(remotePath) {
        call.respondStatic(path)
    }
}

suspend inline fun ApplicationCall.respondStatic(path: Path) {
    if (fileSystem.exists(path)) {
        respond(LocalFileContent(path, ContentType.defaultForFile(path)))
    }
}

fun ContentType.Companion.defaultForFile(path: Path): ContentType =
    ContentType.fromFileExtension(path.name.substringAfter('.', path.name)).selectDefault()

fun List<ContentType>.selectDefault(): ContentType {
    val contentType = firstOrNull() ?: ContentType.Application.OctetStream
    return when {
        contentType.contentType == "text" && contentType.charset() == null -> contentType.withCharset(Charsets.UTF_8)
        else -> contentType
    }
}

class LocalFileContent(
    private val path: Path,
    override val contentType: ContentType = ContentType.defaultForFile(path)
) : OutgoingContent.WriteChannelContent() {

    override val contentLength: Long get() = stat().size ?: -1
    override suspend fun writeTo(channel: ByteWriteChannel) {
        val source = fileSystem.source(path)
        source.use { fileSource ->
            fileSource.buffer().use { bufferedFileSource ->
                val buf = ByteArray(4 * 1024)
                while (true) {
                    val read = bufferedFileSource.read(buf)
                    if (read <= 0) break
                    channel.writeFully(buf, 0, read)
                }
            }
        }
    }

    init {
        if (!fileSystem.exists(path)) {
            throw IllegalStateException("No such file ${path.normalized()}")
        }

        stat().lastModifiedAtMillis?.let {
            versions += LastModifiedVersion(GMTDate(it))
        }
    }

    private fun stat(): FileMetadata {
        return fileSystem.metadata(path)
    }
}