<template>
  <div class="data-table data-table-mobile data-table-api">
    <table>
      <thead>
        <tr>
          <th>{{ t.apiUrl }}</th>
          <th>{{ t.location }}</th>
          <th>{{ t.provider }}</th>
          <th>{{ t.version }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(api, index) in neteaseApiList" :key="api.link">
          <td :data-label="t.apiUrl">{{ api.link }}</td>
          <td :data-label="t.location">{{ getLocation(api.location) }}</td>
          <td :data-label="t.provider">
            <a :href="api.provider.link" target="_blank" rel="noreferrer">
              {{ api.provider.name }}
            </a>
          </td>
          <td :data-label="t.version">
            <img :src="apiVersions[index]" :alt="`${api.link} version`" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vitepress'
import neteaseApiList from '../data/netease-api'
import { getSiteLocale } from '../utils/locale'

const route = useRoute()

const i18n = {
  '/': {
    apiUrl: 'API 地址',
    location: '位置',
    provider: '提供者',
    version: '版本'
  },
  '/en/': {
    apiUrl: 'API URL',
    location: 'Location',
    provider: 'Provider',
    version: 'Version'
  },
  '/zh-tw/': {
    apiUrl: 'API 位址',
    location: '位置',
    provider: '提供者',
    version: '版本'
  },
  '/ja/': {
    apiUrl: 'API URL',
    location: '場所',
    provider: '提供者',
    version: 'バージョン'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))

const t = computed(() => i18n[locale.value])

const getLocation = (location: Record<string, string>) => location[locale.value] ?? location['/']

const FETCHING_BADGE = 'https://img.shields.io/badge/status-Fetching-lightgray'
const apiVersions = ref<string[]>(neteaseApiList.map(() => FETCHING_BADGE))

const getApiVersion = async (link: string): Promise<string> => {
  try {
    const response = await fetch(`${link}/inner/version`)
    const result = await response.json()
    return `https://img.shields.io/badge/inner-v${result.data.version}-blue`
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Unknown error'
    return `https://img.shields.io/badge/error-${encodeURIComponent(message)}-red`
  }
}

onMounted(async () => {
  apiVersions.value = await Promise.all(neteaseApiList.map((api) => getApiVersion(api.link)))
})
</script>

<style scoped>
@media (max-width: 639px) {
  .data-table-api td:first-child {
    word-break: break-all;
  }

  .data-table-api td img {
    max-width: 112px;
  }
}
</style>
