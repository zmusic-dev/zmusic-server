import { computed } from 'vue'
import { useRoute } from 'vitepress'

export type SiteLocale = '/' | '/en/' | '/ja/'

export function getSiteLocale(path: string): SiteLocale {
  if (path.startsWith('/en/')) return '/en/'
  if (path.startsWith('/ja/')) return '/ja/'
  return '/'
}

const messages = {
  '/': {
    note: '其他平台发布更新会稍晚于 GitHub',
    loading: '正在加载下载信息…',
    error: '暂时无法获取下载信息，请稍后再试。',
    download: '下载',
    viewRelease: '查看发布',
    viewBuild: '查看构建',
    devWarning: '开发版可能不稳定，不建议在生产环境使用。',
    loader: '加载器',
    mcVersion: 'Minecraft 版本',
    select: '请选择',
    noResult: '未找到对应的文件，请检查选择。',
    tabs: {
      stable: '稳定版',
      dev: '开发版',
      addon: 'Addon'
    }
  },
  '/en/': {
    note: 'Updates on other platforms may be slightly delayed',
    loading: 'Loading download information...',
    error: 'Download information is currently unavailable.',
    download: 'Download',
    viewRelease: 'View Release',
    viewBuild: 'View Build',
    devWarning: 'Development builds may be unstable and are not recommended for production.',
    loader: 'Loader',
    mcVersion: 'Minecraft Version',
    select: 'Select',
    noResult: 'No matching file found.',
    tabs: {
      stable: 'Stable',
      dev: 'Dev',
      addon: 'Addon'
    }
  },
  '/ja/': {
    note: '他のプラットフォームの更新は GitHub より少し遅れることがあります',
    loading: 'ダウンロード情報を読み込み中...',
    error: 'ダウンロード情報を取得できませんでした。',
    download: 'ダウンロード',
    viewRelease: 'リリースを見る',
    viewBuild: 'ビルドを見る',
    devWarning: '開発ビルドは不安定な可能性があり、本番環境では推奨されません。',
    loader: 'ローダー',
    mcVersion: 'Minecraft バージョン',
    select: '選択',
    noResult: '一致するファイルが見つかりません。',
    tabs: {
      stable: '安定版',
      dev: '開発版',
      addon: 'Addon'
    }
  }
} as const

type Messages = typeof messages
type LocaleMessages = Messages[keyof Messages]

export function useI18n() {
  const route = useRoute()
  const locale = computed(() => getSiteLocale(route.path))

  return computed(() => messages[locale.value] as LocaleMessages)
}

export function useLocale() {
  const route = useRoute()
  return computed(() => getSiteLocale(route.path))
}
