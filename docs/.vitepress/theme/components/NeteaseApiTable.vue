<template>
  <div class="netease-api-tables">
    <!-- 标准版 -->
    <div class="api-section">
      <h3>{{ t.standard }}</h3>
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
            <tr v-for="(api, index) in standardApis" :key="api.link">
              <td :data-label="t.apiUrl">{{ api.link }}</td>
              <td :data-label="t.location">{{ getLocation(api.location) }}</td>
              <td :data-label="t.provider">
                <a :href="api.provider.link" target="_blank" rel="noreferrer">
                  {{ api.provider.name }}
                </a>
              </td>
              <td :data-label="t.version">
                <img :src="standardVersions[index]" :alt="`${api.link} version`" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 增强版 -->
    <div class="api-section">
      <h3>{{ t.enhanced }}</h3>
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
            <tr v-for="(api, index) in enhancedApis" :key="api.link">
              <td :data-label="t.apiUrl">{{ api.link }}</td>
              <td :data-label="t.location">{{ getLocation(api.location) }}</td>
              <td :data-label="t.provider">
                <a :href="api.provider.link" target="_blank" rel="noreferrer">
                  {{ api.provider.name }}
                </a>
              </td>
              <td :data-label="t.version">
                <img :src="enhancedVersions[index]" :alt="`${api.link} version`" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
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
    version: '版本',
    standard: '标准版',
    enhanced: '增强版'
  },
  '/en/': {
    apiUrl: 'API URL',
    location: 'Location',
    provider: 'Provider',
    version: 'Version',
    standard: 'Standard',
    enhanced: 'Enhanced'
  },
  '/zh-tw/': {
    apiUrl: 'API 位址',
    location: '位置',
    provider: '提供者',
    version: '版本',
    standard: '標準版',
    enhanced: '增強版'
  },
  '/ja/': {
    apiUrl: 'API URL',
    location: '場所',
    provider: '提供者',
    version: 'バージョン',
    standard: '標準版',
    enhanced: '拡張版'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))

const t = computed(() => i18n[locale.value])

const getLocation = (location: Record<string, string>) => location[locale.value] ?? location['/']

const FETCHING_BADGE = 'https://img.shields.io/badge/status-Fetching-lightgray'

const standardApis = computed(() => neteaseApiList.filter((api) => api.type === 'standard'))
const enhancedApis = computed(() => neteaseApiList.filter((api) => api.type === 'enhanced'))

const standardVersions = ref<string[]>(standardApis.value.map(() => FETCHING_BADGE))
const enhancedVersions = ref<string[]>(enhancedApis.value.map(() => FETCHING_BADGE))

const getApiVersion = async (link: string, isEnhanced: boolean): Promise<string> => {
  try {
    const response = await fetch(`${link}/inner/version`)
    const result = await response.json()
    const color = isEnhanced ? 'purple' : 'blue'
    return `https://img.shields.io/badge/inner-v${result.data.version}-${color}`
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Unknown error'
    return `https://img.shields.io/badge/error-${encodeURIComponent(message)}-red`
  }
}

onMounted(async () => {
  standardVersions.value = await Promise.all(
    standardApis.value.map((api) => getApiVersion(api.link, false))
  )
  enhancedVersions.value = await Promise.all(
    enhancedApis.value.map((api) => getApiVersion(api.link, true))
  )
})
</script>

<style scoped>
.netease-api-tables {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

@media (max-width: 639px) {
  .data-table-api td:first-child {
    word-break: break-all;
  }

  .data-table-api td img {
    max-width: 112px;
  }
}
</style>
