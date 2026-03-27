<template>
  <div class="mod-download">
    <div v-if="loading" class="mod-loading">{{ t.loading }}</div>
    <div v-else-if="error" class="mod-error">{{ t.error }}</div>
    <template v-else>
      <div class="mod-selectors">
        <div class="selector-group">
          <label class="selector-label">{{ t.loader }}</label>
          <div class="custom-select" :class="{ active: openLoader }">
            <button class="select-trigger" @click="openLoader = !openLoader; openMcVersion = false">
              <span>{{ selectedLoader || t.select }}</span>
              <span class="arrow" :class="{ up: openLoader }">▼</span>
            </button>
            <div v-if="openLoader" class="select-dropdown">
              <button
                v-for="l in loaders"
                :key="l"
                class="select-option"
                :class="{ selected: l === selectedLoader }"
                @click="selectLoader(l)"
              >
                {{ l }}
              </button>
            </div>
          </div>
        </div>
        <div class="selector-group">
          <label class="selector-label">{{ t.mcVersion }}</label>
          <div class="custom-select" :class="{ active: openMcVersion }">
            <button class="select-trigger" @click="openMcVersion = !openMcVersion; openLoader = false">
              <span>{{ selectedMcVersion || t.select }}</span>
              <span class="arrow" :class="{ up: openMcVersion }">▼</span>
            </button>
            <div v-if="openMcVersion" class="select-dropdown">
              <button
                v-for="m in filteredMcVersions"
                :key="m"
                class="select-option"
                :class="{ selected: m === selectedMcVersion }"
                @click="selectMcVersion(m)"
              >
                {{ m }}
              </button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="selectedFile" class="mod-result">
        <div class="result-info">
          <span class="result-filename">{{ selectedFile.name }}</span>
        </div>
        <a :href="selectedFile.url" class="btn-download" download>
          {{ t.download }}
        </a>
      </div>
      <div v-else class="mod-no-result">{{ t.noResult }}</div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useData, useRoute } from 'vitepress'
import { getSiteLocale } from '../utils/locale'

interface ModFile {
  name: string
  url: string
  version: string
  loader: string
  mcVersion: string
}

const route = useRoute()

const i18n = {
  '/': {
    loader: '加载器',
    mcVersion: 'Minecraft 版本',
    download: '下载',
    select: '请选择',
    loading: '正在加载版本信息…',
    error: '获取版本信息失败，请稍后再试。',
    noResult: '未找到对应的文件，请检查选择。'
  },
  '/en/': {
    loader: 'Loader',
    mcVersion: 'Minecraft Version',
    download: 'Download',
    select: 'Select',
    loading: 'Loading version information...',
    error: 'Failed to fetch version information.',
    noResult: 'No matching file found.'
  },
  '/zh-tw/': {
    loader: '載入器',
    mcVersion: 'Minecraft 版本',
    download: '下載',
    select: '請選擇',
    loading: '正在載入版本資訊…',
    error: '取得版本資訊失敗，請稍後再試。',
    noResult: '未找到對應的檔案。'
  },
  '/ja/': {
    loader: 'ローダー',
    mcVersion: 'Minecraft バージョン',
    download: 'ダウンロード',
    select: '選択',
    loading: 'バージョン情報を読み込み中...',
    error: 'バージョン情報の取得に失敗しました。',
    noResult: '一致するファイルが見つかりません。'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))
const t = computed(() => i18n[locale.value])

const { theme } = useData()
const REPO = computed(() => theme.value.modRepo as string)

const files = ref<ModFile[]>([])
const loading = ref(true)
const error = ref(false)

const selectedLoader = ref<string>('')
const selectedMcVersion = ref<string>('')

const openLoader = ref(false)
const openMcVersion = ref(false)

const closeAllDropdowns = () => {
  openLoader.value = false
  openMcVersion.value = false
}

const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (!target.closest('.custom-select')) {
    closeAllDropdowns()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const selectLoader = (l: string) => {
  selectedLoader.value = l
  openLoader.value = false
}

const selectMcVersion = (m: string) => {
  selectedMcVersion.value = m
  openMcVersion.value = false
}

// Get latest version
const latestVersion = computed(() => {
  const versions = new Set(files.value.map(f => f.version))
  return Array.from(versions).sort((a, b) => b.localeCompare(a))[0] || ''
})

// Filter files to only latest version
const latestFiles = computed(() => {
  return files.value.filter(f => f.version === latestVersion.value)
})

const loaders = computed(() => {
  const l = new Set(latestFiles.value.map(f => f.loader))
  return Array.from(l).sort()
})

const filteredMcVersions = computed(() => {
  const filtered = latestFiles.value.filter(f => {
    if (selectedLoader.value && f.loader !== selectedLoader.value) return false
    return true
  })
  const m = new Set(filtered.map(f => f.mcVersion))
  return Array.from(m).sort((a, b) => {
    const parseVer = (v: string) => v.split('.').map(Number)
    const aParts = parseVer(a)
    const bParts = parseVer(b)
    for (let i = 0; i < Math.max(aParts.length, bParts.length); i++) {
      const aVal = aParts[i] || 0
      const bVal = bParts[i] || 0
      if (aVal !== bVal) return bVal - aVal
    }
    return 0
  })
})

const selectedFile = computed(() => {
  return latestFiles.value.find(f =>
    f.loader === selectedLoader.value &&
    f.mcVersion === selectedMcVersion.value
  )
})

// Auto-select first option when list changes
watch(loaders, (l) => {
  if (l.length && !l.includes(selectedLoader.value)) {
    selectedLoader.value = l[0]
  }
})

watch(filteredMcVersions, (m) => {
  if (m.length && !m.includes(selectedMcVersion.value)) {
    selectedMcVersion.value = m[0]
  }
})

const parseFileName = (name: string): { loader: string; mcVersion: string; version: string } | null => {
  const match = name.match(/^zmusic-(fabric|forge)-([\d.]+)-([\d.]+)\.jar$/)
  if (!match) return null
  return {
    loader: match[1],
    mcVersion: match[2],
    version: match[3]
  }
}

onMounted(async () => {
  try {
    const response = await fetch(
      `https://api.github.com/repos/${REPO.value}/releases?per_page=100`
    )
    const data = await response.json()

    const allFiles: ModFile[] = []
    for (const release of data) {
      for (const asset of release.assets) {
        const parsed = parseFileName(asset.name)
        if (parsed) {
          allFiles.push({
            name: asset.name,
            url: asset.browser_download_url,
            version: parsed.version,
            loader: parsed.loader,
            mcVersion: parsed.mcVersion
          })
        }
      }
    }

    files.value = allFiles
    loading.value = false
  } catch (e) {
    console.error(e)
    error.value = true
    loading.value = false
  }
})
</script>

<style scoped>
.mod-download {
  margin: 1rem 0;
}

.mod-loading,
.mod-error,
.mod-no-result {
  text-align: center;
  padding: 2rem;
  color: var(--vp-c-text-2);
}

.mod-error {
  color: var(--vp-c-danger-1);
}

.mod-selectors {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.selector-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.selector-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--vp-c-text-1);
}

.custom-select {
  position: relative;
}

.select-trigger {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: transparent;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  color: var(--vp-c-text-1);
  cursor: pointer;
  transition: border-color 0.25s;
}

.select-trigger:hover {
  border-color: var(--vp-c-brand-1);
}

.custom-select.active .select-trigger {
  border-color: var(--vp-c-brand-1);
}

.arrow {
  font-size: 0.75rem;
  transition: transform 0.2s;
  color: var(--vp-c-text-2);
}

.arrow.up {
  transform: rotate(180deg);
}

.select-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  z-index: 100;
  max-height: 240px;
  overflow-y: auto;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: var(--vp-c-bg-elv);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.select-option {
  display: block;
  width: 100%;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  text-align: left;
  border: none;
  background: transparent;
  color: var(--vp-c-text-1);
  cursor: pointer;
  transition: background 0.15s;
}

.select-option:hover {
  background: var(--vp-c-default-soft);
}

.select-option.selected {
  color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
}

.mod-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.25rem;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: transparent;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: border-color 0.25s, box-shadow 0.25s;
}

.mod-result:hover {
  border-color: var(--vp-c-brand-1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.result-filename {
  font-family: var(--vp-font-family-mono);
  font-size: 0.875rem;
  color: var(--vp-c-text-1);
  word-break: break-all;
}

.btn-download {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--vp-c-white);
  background: var(--vp-c-brand-1);
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: background 0.2s;
  white-space: nowrap;
}

.btn-download:hover {
  background: var(--vp-c-brand-2);
}

@media (max-width: 639px) {
  .mod-selectors {
    grid-template-columns: 1fr;
  }

  .mod-result {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }

  .btn-download {
    justify-content: center;
  }
}
</style>
