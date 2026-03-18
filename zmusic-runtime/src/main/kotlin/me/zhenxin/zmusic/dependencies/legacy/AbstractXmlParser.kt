package me.zhenxin.zmusic.dependencies.legacy

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.text.ParseException
import java.util.regex.Pattern

abstract class AbstractXmlParser {
    companion object {
        private val substitutionPattern = Pattern.compile("\\$\\{([^}]+)}")

        @JvmStatic
        @Throws(ParseException::class)
        protected fun find(name: String, node: Element): String {
            return find(name, node, null)
        }

        @JvmStatic
        @Throws(ParseException::class)
        protected fun find(name: String, node: Element, def: String?): String {
            var list = node.childNodes
            for (index in 0 until list.length) {
                val currentNode = list.item(index)
                if (currentNode.nodeName == name) {
                    return replaceVariablesOrDefault(currentNode, node, def)
                }
            }
            list = node.getElementsByTagName(name)
            if (list.length > 0) {
                return replaceVariablesOrDefault(list.item(0), node, def)
            }
            if (def == null) {
                throw ParseException("Unable to find required tag '$name' in node", -1)
            }
            return def
        }

        @Throws(ParseException::class)
        private fun replaceVariablesOrDefault(targetNode: Node, ownerNode: Element, def: String?): String {
            return try {
                replaceVariables(targetNode.textContent, ownerNode.ownerDocument.documentElement)
            } catch (exception: ParseException) {
                def ?: throw exception
            }
        }

        @Throws(ParseException::class)
        private fun replaceVariables(text: String, pom: Element): String {
            val matcher = substitutionPattern.matcher(text)
            var result = text
            while (matcher.find()) {
                result = matcher.replaceFirst(getReplacement(matcher.group(1), pom))
            }
            return result
        }

        @Throws(ParseException::class)
        private fun getReplacement(key: String, pom: Element): String {
            return when {
                key.startsWith("project.") -> find(key.removePrefix("project."), pom)
                key.startsWith("pom.") -> find(key.removePrefix("pom."), pom)
                else -> throw ParseException("Unknown variable '$key'", -1)
            }
        }
    }
}
