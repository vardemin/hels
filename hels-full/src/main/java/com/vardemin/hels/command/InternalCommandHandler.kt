package com.vardemin.hels.command

internal interface InternalCommandHandler<T: InternalCommand> {
    fun handle(command: T)
}