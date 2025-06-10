package me.zhenxin.zmusic.utils

import cn.hutool.core.net.url.UrlBuilder
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import cn.hutool.json.JSONObject
import me.zhenxin.zmusic.ZMusicConstants
import me.zhenxin.zmusic.logger

/**
 * HTTP 工具
 *
 * @author 真心
 * @since 2023/7/24 10:04
 */

/**
 * GET 获取
 * @param url 链接
 * @param params 参数
 * @param headers 请求头
 * @param cache 是否使用缓存
 */
fun httpGet(
    url: String,
    params: Map<String, String> = mapOf(),
    headers: Map<String, String> = mapOf(),
    cache: Boolean = true
): String {
    val httpUrl = buildUrl(url, cache, params)
    val request = HttpUtil.createGet(httpUrl)
    return request(request, headers)
}

/**
 * POST 获取
 * @param url 连接
 * @param data 参数
 * @param headers 请求头
 * @param cache 是否使用缓存
 */
fun httpPost(
    url: String,
    data: JSONObject,
    headers: Map<String, String> = mapOf(),
    cache: Boolean = true
): String {
    val httpUrl = buildUrl(url, cache)
    val request = HttpUtil.createPost(httpUrl).body(data.toString())
    return request(request, headers)
}

private fun buildUrl(url: String, cache: Boolean, params: Map<String, String> = mapOf()): String {
    val builder = UrlBuilder.ofHttp(url)
    params.map {
        builder.addQuery(it.key, it.value)
    }
    if (!cache) {
        builder.addQuery("t", System.currentTimeMillis())
    }
    return builder.build()
}

private fun request(request: HttpRequest, headers: Map<String, String> = mapOf()): String {
    request.header("User-Agent", "ZMusic/${ZMusicConstants.PLUGIN_VERSION}")
    headers.forEach {
        request.header(it.key, it.value)
    }
    logger.debug("Request: ${request.method} ${request.url}")
    if (request.method == Method.POST) {
        logger.debug("Request body: ${String(request.bodyBytes())}")
    }
    val response = request.execute()
    val body = response.body()
    if (response.isOk) {
        logger.debug("Response: $body")
    } else {
        val errorMessage = "Request failed: ${response.status}, body: $body"
        logger.debug(errorMessage)
    }
    return body
}