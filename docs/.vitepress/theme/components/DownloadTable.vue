<template>
  <div class="download-section">
    <div v-if="loading" class="download-loading">{{ t.loading }}</div>
    <div v-else-if="!list.length" class="download-empty">{{ t.empty }}</div>
    <div v-else class="download-cards">
      <div v-for="item in list" :key="item.source.link" class="download-card">
        <div class="card-header">
          <a :href="item.source.link" target="_blank" rel="noreferrer" class="card-title">
            {{ item.source.name }}
          </a>
          <span class="card-version">{{ item.version }}</span>
        </div>
        <div class="card-desc">
          <span class="desc-main">{{ item.descMain }}</span>
          <span v-if="item.descSub" class="desc-tag">{{ item.descSub }}</span>
        </div>
        <div class="card-actions">
          <a :href="item.release" target="_blank" rel="noreferrer" class="btn-secondary">
            {{ t.viewRelease }}
          </a>
          <div class="download-buttons">
            <a
              v-for="downloadItem in item.download"
              :key="downloadItem.link"
              :href="downloadItem.link"
              target="_blank"
              rel="noreferrer"
              class="btn-primary"
            >
              {{ downloadItem.name }}
            </a>
          </div>
        </div>
      </div>
    </div>
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
  descMain: string
  descSub?: string
  version: string
  release: string
  download: DownloadFile[]
}

const route = useRoute()

const i18n = {
  '/': {
    download: '下载',
    viewRelease: '查看',
    loading: '正在加载下载信息…',
    empty: '暂时无法获取下载信息，请稍后再试。',
    desc: {
      githubReleases: { main: '最新稳定发布版本' },
      githubActions: { main: '最新开发版本', sub: '不建议生产使用' },
      codeberg: { main: '最新稳定版本', sub: '发布可能延迟' },
      gitee: { main: '最新稳定版本', sub: '发布可能延迟' },
      spigotmc: { main: '最新稳定版本', sub: '发布可能延迟' }
    }
  },
  '/en/': {
    download: 'Download',
    viewRelease: 'View',
    loading: 'Loading download information...',
    empty: 'Download information is currently unavailable.',
    desc: {
      githubReleases: { main: 'Latest stable release' },
      githubActions: { main: 'Latest development build', sub: 'not recommended for production' },
      codeberg: { main: 'Latest stable release', sub: 'may be delayed' },
      gitee: { main: 'Latest stable release', sub: 'may be delayed' },
      spigotmc: { main: 'Latest stable release', sub: 'may be delayed' }
    }
  },
  '/zh-tw/': {
    download: '下載',
    viewRelease: '查看',
    loading: '正在載入下載資訊…',
    empty: '暫時無法取得下載資訊，請稍後再試。',
    desc: {
      githubReleases: { main: '最新穩定發布版本' },
      githubActions: { main: '最新開發版本', sub: '不建議生產使用' },
      codeberg: { main: '最新穩定版本', sub: '發布可能延遲' },
      gitee: { main: '最新穩定版本', sub: '發布可能延遲' },
      spigotmc: { main: '最新穩定版本', sub: '發布可能延遲' }
    }
  },
  '/ja/': {
    download: 'ダウンロード',
    viewRelease: '表示',
    loading: 'ダウンロード情報を読み込み中...',
    empty: 'ダウンロード情報を取得できませんでした。',
    desc: {
      githubReleases: { main: '最新の安定版リリース' },
      githubActions: { main: '最新の開発ビルド', sub: '本番環境では推奨されません' },
      codeberg: { main: '最新の安定版リリース', sub: '公開が遅れる可能性があります' },
      gitee: { main: '最新の安定版リリース', sub: '公開が遅れる可能性があります' },
      spigotmc: { main: '最新の安定版リリース', sub: '公開が遅れる可能性があります' }
    }
  }
} as const

const locale = computed(() => getSiteLocale(route.path))

const t = computed(() => i18n[locale.value])

const list = ref<DownloadItem[]>([])
const loading = ref(true)

const REPO = 'zmusic-dev/zmusic-server'

const safeFetch = async <T,>(fn: () => Promise<T>): Promise<T | null> => {
  try {
    return await fn()
  } catch (error) {
    console.error(error)
    return null
  }
}

const githubLatestRelease = async (): Promise<DownloadItem> => {
  const response = await fetch(`https://api.github.com/repos/${REPO}/releases/latest`)
  const data = await response.json()
  const desc = t.value.desc.githubReleases

  return {
    source: {
      name: 'GitHub Releases',
      link: `https://github.com/${REPO}/releases`
    },
    descMain: desc.main,
    descSub: desc.sub,
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
    `https://api.github.com/repos/${REPO}/actions/workflows/dev.yml/runs?per_page=1&status=success`
  )
  const data = await response.json()
  const run = data.workflow_runs[0]
  const desc = t.value.desc.githubActions

  const artifactsResponse = await fetch(
    `https://api.github.com/repos/${REPO}/actions/runs/${run.id}/artifacts`
  )
  const artifactsData = await artifactsResponse.json()

  return {
    source: {
      name: 'GitHub Actions',
      link: `https://github.com/${REPO}/actions/workflows/dev.yml`
    },
    descMain: desc.main,
    descSub: desc.sub,
    version: `v4.0.0-dev.${run.head_sha.substring(0, 7)}`,
    release: run.html_url,
    download: artifactsData.artifacts.map((item: { name: string; id: number }) => ({
      name: `${item.name}.zip`,
      link: `https://github.com/${REPO}/actions/runs/${run.id}/artifacts/${item.id}`
    }))
  }
}

const codebergLatestRelease = async (): Promise<DownloadItem> => {
  const response = await fetch(`https://codeberg.org/api/v1/repos/${REPO}/releases/latest`)
  const data = await response.json()
  const desc = t.value.desc.codeberg

  return {
    source: {
      name: 'Codeberg',
      link: `https://codeberg.org/${REPO}/releases`
    },
    descMain: desc.main,
    descSub: desc.sub,
    version: data.tag_name,
    release: data.html_url,
    download: data.assets
      .filter((item: { name: string }) => item.name.endsWith('.jar'))
      .map((item: { name: string; browser_download_url: string }) => ({
        name: item.name,
        link: item.browser_download_url
      }))
  }
}

const giteeLatestRelease = async (): Promise<DownloadItem> => {
  const response = await fetch(`https://gitee.com/api/v5/repos/${REPO}/releases/latest`)
  const data = await response.json()
  const desc = t.value.desc.gitee

  return {
    source: {
      name: 'Gitee',
      link: `https://gitee.com/${REPO}/releases`
    },
    descMain: desc.main,
    descSub: desc.sub,
    version: data.tag_name,
    release: `https://gitee.com/${REPO}/releases/tag/${data.tag_name}`,
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
  const desc = t.value.desc.spigotmc

  return {
    source: {
      name: 'SpigotMC',
      link: 'https://www.spigotmc.org/resources/83027/'
    },
    descMain: desc.main,
    descSub: desc.sub,
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
    safeFetch(codebergLatestRelease),
    safeFetch(giteeLatestRelease),
    safeFetch(spigotLatestVersion)
  ])

  list.value = results.filter((item): item is DownloadItem => item !== null)
  loading.value = false
})
</script>

<style scoped>
.download-section {
  margin: 1rem 0;
}

.download-loading,
.download-empty {
  text-align: center;
  padding: 2rem;
  color: var(--vp-c-text-2);
}

.download-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1rem;
}

.download-card {
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  padding: 1.25rem;
  background: transparent;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: border-color 0.25s, box-shadow 0.25s;
}

.download-card:hover {
  border-color: var(--vp-c-brand-1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  gap: 0.5rem;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--vp-c-text-1);
  text-decoration: none;
}

.card-title:hover {
  color: var(--vp-c-brand-1);
}

.card-version {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
  padding: 0.2rem 0.6rem;
  border-radius: 6px;
  white-space: nowrap;
}

.card-desc {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.desc-main {
  color: var(--vp-c-text-2);
}

.desc-tag {
  font-size: 0.75rem;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  background: var(--vp-c-warning-soft);
  color: var(--vp-c-warning-1);
  white-space: nowrap;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-secondary {
  font-size: 0.85rem;
  color: var(--vp-c-text-2);
  text-decoration: none;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  border: 1px solid var(--vp-c-divider);
  transition: all 0.2s;
}

.btn-secondary:hover {
  color: var(--vp-c-text-1);
  border-color: var(--vp-c-text-2);
}

.download-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn-primary {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--vp-c-white);
  background: var(--vp-c-brand-1);
  text-decoration: none;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: var(--vp-c-brand-2);
}

@media (max-width: 639px) {
  .download-cards {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .download-buttons {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    text-align: center;
    justify-content: center;
  }
}
</style>
