package com.tagaev.myapplication.domain

import com.tagaev.myapplication.Variables.NAME_OF_LOG_FILE


fun runCommand(text: String): String {
    return when {
        text.isEmpty() -> "Empty command"
        text.startsWith("r ") -> {
            NAME_OF_LOG_FILE = text.removePrefix("r ").trim() + NAME_OF_LOG_FILE
            "recording with name: [${NAME_OF_LOG_FILE}]"
        }
        text.startsWith("s ") -> "Starting..."
        text.startsWith("stop") -> "Stopping..."
        text.startsWith("clear") -> "Clearing..."
        else -> "Undefined Command: '$text'"
    }
}