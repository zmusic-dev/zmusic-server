<template>
  <div class="custom-block warning v4-warning">
    <p class="custom-block-title">{{ t.title }}</p>
    <p>{{ t.description }}</p>
    <p>
      {{ t.supportedPrefix }}
      <a :href="withBase(t.link)">{{ t.linkText }}</a>
    </p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute, withBase } from 'vitepress'
import { getSiteLocale } from '../utils/locale'

const route = useRoute()

const i18n = {
  '/': {
    title: 'V4 版本文档',
    description: '此版本仍在开发中，可能存在错误或不完善的地方。',
    supportedPrefix: '当前长期支持版本为 V2，您可以查看 ',
    linkText: 'V2 文档',
    link: '/v2/'
  },
  '/en/': {
    title: 'V4 Documentation',
    description: 'This version is still under development and may contain errors or incomplete features.',
    supportedPrefix: 'The current long-term support version is V2. See ',
    linkText: 'V2 Docs',
    link: '/en/v2/'
  },
  '/zh-tw/': {
    title: 'V4 版本文件',
    description: '此版本仍在開發中，可能存在錯誤或不完善的地方。',
    supportedPrefix: '目前長期支援版本為 V2，您可以查看 ',
    linkText: 'V2 文件',
    link: '/zh-tw/v2/'
  },
  '/ja/': {
    title: 'V4 ドキュメント',
    description: 'このバージョンはまだ開発中であり、エラーや不完全な部分がある可能性があります。',
    supportedPrefix: '現在の長期サポートバージョンは V2 です。 ',
    linkText: 'V2 ドキュメント',
    link: '/ja/v2/'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))

const t = computed(() => i18n[locale.value])
</script>
