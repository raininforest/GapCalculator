package com.github.raininforest.web

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement

/** Tiny DOM builder: creates [tag], fills it with [classes]/[text]/[build] and appends it to the receiver. */
fun Element.add(
    tag: String,
    classes: String = "",
    text: String? = null,
    build: Element.() -> Unit = {}
): Element {
    val child = document.createElement(tag)
    if (classes.isNotEmpty()) child.className = classes
    if (text != null) child.textContent = text
    child.build()
    appendChild(child)
    return child
}

fun Element.button(classes: String, text: String, onClick: () -> Unit): HTMLButtonElement =
    (add("button", classes, text) as HTMLButtonElement).also { it.onclick = { onClick() } }

fun Element.input(classes: String = "", value: String = "", onInput: (String) -> Unit): HTMLInputElement =
    (add("input", classes) as HTMLInputElement).also { input ->
        input.value = value
        input.oninput = { onInput(input.value) }
    }

fun Element.clear() {
    while (firstChild != null) removeChild(firstChild!!)
}
