<script setup lang="ts">
import { useData } from 'vitepress'
import { ref, onMounted, watch } from 'vue'

const { isDark, lang } = useData()
const container = ref<HTMLDivElement>()

const giscusLangMap: Record<string, string> = {
  'zh-CN': 'zh-CN',
  'zh-TW': 'zh-TW',
  'en-US': 'en',
  'ja-JP': 'ja'
}

function getGiscusLang(locale: string) {
  return giscusLangMap[locale] || 'en'
}

function loadGiscus() {
  if (!container.value) return
  container.value.innerHTML = ''

  const script = document.createElement('script')
  script.src = 'https://giscus.app/client.js'
  script.setAttribute('data-repo', 'zmusic-dev/zmusic-comments')
  script.setAttribute('data-repo-id', 'R_kgDORwUV4A')
  script.setAttribute('data-category', 'comments')
  script.setAttribute('data-category-id', 'DIC_kwDORwUV4M4C5PI7')
  script.setAttribute('data-mapping', 'pathname')
  script.setAttribute('data-strict', '0')
  script.setAttribute('data-reactions-enabled', '1')
  script.setAttribute('data-emit-metadata', '0')
  script.setAttribute('data-input-position', 'bottom')
  script.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
  script.setAttribute('data-lang', getGiscusLang(lang.value))
  script.setAttribute('data-loading', 'lazy')
  script.setAttribute('crossorigin', 'anonymous')
  script.async = true

  container.value.appendChild(script)
}

onMounted(loadGiscus)

watch(isDark, loadGiscus)
</script>

<template>
  <div ref="container" class="giscus-container" />
</template>

<style scoped>
.giscus-container {
  margin-top: 2rem;
}
</style>
