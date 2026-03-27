export type SiteIconName =
  | 'home'
  | 'lightbulb'
  | 'circle-help'
  | 'circle-info'
  | 'music'
  | 'message-circle'
  | 'messages-square'
  | 'book-open'
  | 'signpost'
  | 'terminal'
  | 'shield'
  | 'settings'

const localeLabels = new Set(['简体中文', 'English', '繁體中文', '日本語'])

const homeLabels = new Set(['首页', 'Home', '首頁', 'ホーム'])

const docIconMap: Record<string, SiteIconName> = {
  '/': 'home',
  '/guide': 'lightbulb',
  '/guide/introduction': 'circle-info',
  '/guide/getting-started': 'signpost',
  '/guide/commands': 'terminal',
  '/guide/permissions': 'shield',
  '/guide/config': 'settings',
  '/faq': 'circle-help',
  '/v2': 'book-open',
  '/v2/faq': 'circle-help',
  '/netease-api': 'music',
  '/netease-api/public': 'music',
  '/netease-api/standard': 'music',
  '/netease-api/enhanced': 'music',
  '/netease-api-standard': 'music',
  '/netease-api-enhanced': 'music'
}

function normalizeDocLink(link: string) {
  const path = link.split(/[?#]/, 1)[0]
  const withoutHtml = path.replace(/\.html$/, '')
  const withoutLocale = withoutHtml.replace(/^\/(?:en|zh-tw|ja)(?=\/|$)/, '')

  if (withoutLocale === '') return '/'
  if (withoutLocale !== '/' && withoutLocale.endsWith('/')) {
    return withoutLocale.slice(0, -1)
  }

  return withoutLocale
}

export function getSiteIconName(link?: string, text?: string): SiteIconName | null {
  if (!link) return null
  if (text && localeLabels.has(text)) return null

  if (/discord\.gg/.test(link)) return 'messages-square'
  if (/jq\.qq\.com/.test(link)) return 'message-circle'

  if (homeLabels.has(text ?? '')) return 'home'

  return docIconMap[normalizeDocLink(link)] ?? null
}
