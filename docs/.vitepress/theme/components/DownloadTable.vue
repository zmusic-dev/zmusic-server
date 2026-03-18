<template>
  <div class="data-table data-table-mobile data-table-download">
    <table>
      <thead>
        <tr>
          <th>{{ t.channel }}</th>
          <th>{{ t.version }}</th>
          <th>{{ t.release }}</th>
          <th>{{ t.download }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="4">{{ t.loading }}</td>
        </tr>
        <tr v-else-if="!list.length">
          <td colspan="4">{{ t.empty }}</td>
        </tr>
        <tr v-for="item in list" :key="item.source.link">
          <td :data-label="t.channel">
            <a :href="item.source.link" target="_blank" rel="noreferrer">
              {{ item.source.name }}
            </a>
          </td>
          <td :data-label="t.version">{{ item.version }}</td>
          <td :data-label="t.release">
            <a :href="item.release" target="_blank" rel="noreferrer">
              {{ t.viewRelease }}
            </a>
          </td>
          <td class="download-links" :data-label="t.download">
            <a
              v-for="downloadItem in item.download"
              :key="downloadItem.link"
              :href="downloadItem.link"
              target="_blank"
              rel="noreferrer"
            >
              {{ downloadItem.name }}
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vitepress'
import { getSiteLocale } from '../utils/locale'

interface DownloadFile {
  name: string
  link: string
}

interface DownloadItem {
  source: { name: string; link: string }
  version: string
  release: string
  download: DownloadFile[]
}

const route = useRoute()

const i18n = {
  '/': {
    channel: '渠道',
    version: '版本',
    release: '发布',
    download: '下载',
    viewRelease: '点击查看',
    loading: '正在加载下载信息…',
    empty: '暂时无法获取下载信息，请稍后再试。'
  },
  '/en/': {
    channel: 'Channel',
    version: 'Version',
    release: 'Release',
    download: 'Download',
    viewRelease: 'View',
    loading: 'Loading download information...',
    empty: 'Download information is currently unavailable.'
  },
  '/zh-tw/': {
    channel: '管道',
    version: '版本',
    release: '發布',
    download: '下載',
    viewRelease: '點擊查看',
    loading: '正在載入下載資訊…',
    empty: '暫時無法取得下載資訊，請稍後再試。'
  },
  '/ja/': {
    channel: 'チャンネル',
    version: 'バージョン',
    release: 'リリース',
    download: 'ダウンロード',
    viewRelease: '表示',
    loading: 'ダウンロード情報を読み込み中...',
    empty: 'ダウンロード情報を取得できませんでした。'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))

const t = computed(() => i18n[locale.value])

const list = ref<DownloadItem[]>([])
const loading = ref(true)

const GITHUB_REPO = 'zmusic-dev/zmusic-server'

const safeFetch = async <T,>(fn: () => Promise<T>): Promise<T | null> => {
  try {
    return await fn()
  } catch (error) {
    console.error(error)
    return null
  }
}

const githubLatestRelease = async (): Promise<DownloadItem> => {
  const response = await fetch(`https://api.github.com/repos/${GITHUB_REPO}/releases/latest`)
  const data = await response.json()

  return {
    source: {
      name: 'GitHub Releases',
      link: `https://github.com/${GITHUB_REPO}/releases`
    },
    version: data.tag_name,
    release: data.html_url,
    download: data.assets.map((item: { name: string; browser_download_url: string }) => ({
      name: item.name,
      link: item.browser_download_url
    }))
  }
}

const githubLatestBuild = async (): Promise<DownloadItem> => {
  const response = await fetch(
    `https://api.github.com/repos/${GITHUB_REPO}/actions/workflows/dev.yml/runs?per_page=1&status=success`
  )
  const data = await response.json()
  const run = data.workflow_runs[0]

  const artifactsResponse = await fetch(
    `https://api.github.com/repos/${GITHUB_REPO}/actions/runs/${run.id}/artifacts`
  )
  const artifactsData = await artifactsResponse.json()

  return {
    source: {
      name: 'GitHub Actions',
      link: `https://github.com/${GITHUB_REPO}/actions/workflows/dev.yml`
    },
    version: `v4.0.0-dev.${run.head_sha.substring(0, 7)}`,
    release: run.html_url,
    download: artifactsData.artifacts.map((item: { name: string; id: number }) => ({
      name: `${item.name}.zip`,
      link: `https://github.com/${GITHUB_REPO}/actions/runs/${run.id}/artifacts/${item.id}`
    }))
  }
}

const giteeLatestRelease = async (): Promise<DownloadItem> => {
  const response = await fetch(`https://gitee.com/api/v5/repos/${GITHUB_REPO}/releases/latest`)
  const data = await response.json()

  return {
    source: {
      name: 'Gitee',
      link: `https://gitee.com/${GITHUB_REPO}/releases`
    },
    version: data.tag_name,
    release: `https://gitee.com/${GITHUB_REPO}/releases/tag/${data.tag_name}`,
    download: data.assets
      .filter((item: { name: string }) => item.name.endsWith('.jar'))
      .map((item: { name: string; browser_download_url: string }) => ({
        name: item.name,
        link: item.browser_download_url
      }))
  }
}

const spigotLatestVersion = async (): Promise<DownloadItem> => {
  const [versionResponse, updateResponse] = await Promise.all([
    fetch('https://api.spiget.org/v2/resources/83027/versions/latest'),
    fetch('https://api.spiget.org/v2/resources/83027/updates/latest')
  ])
  const versionData = await versionResponse.json()
  const updateData = await updateResponse.json()

  return {
    source: {
      name: 'SpigotMC',
      link: 'https://www.spigotmc.org/resources/83027/'
    },
    version: versionData.name,
    release: `https://www.spigotmc.org/resources/83027/update?update=${updateData.id}`,
    download: [
      {
        name: 'ZMusic Latest',
        link: `https://www.spigotmc.org/resources/83027/download?version=${versionData.id}`
      }
    ]
  }
}

onMounted(async () => {
  const results = await Promise.all([
    safeFetch(githubLatestRelease),
    safeFetch(githubLatestBuild),
    safeFetch(giteeLatestRelease),
    safeFetch(spigotLatestVersion)
  ])

  list.value = results.filter((item): item is DownloadItem => item !== null)
  loading.value = false
})
</script>

<style scoped>
.download-links {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

@media (max-width: 639px) {
  .data-table-download td:nth-child(2),
  .data-table-download td:nth-child(4) {
    word-break: break-all;
  }

  .download-links {
    gap: 0.5rem;
  }
}
</style>
