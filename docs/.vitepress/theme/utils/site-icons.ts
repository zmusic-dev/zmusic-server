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

export const siteIcons: Record<SiteIconName, string> = {
  home: `
    <path d="M3 10.5L12 3l9 7.5" />
    <path d="M5 9.5V21h14V9.5" />
  `,
  lightbulb: `
    <path d="M9 18h6" />
    <path d="M10 22h4" />
    <path d="M12 2a7 7 0 0 0-4 12.7c.6.4 1 1 1.2 1.8h5.6c.2-.8.6-1.4 1.2-1.8A7 7 0 0 0 12 2Z" />
  `,
  'circle-help': `
    <circle cx="12" cy="12" r="9" />
    <path d="M9.6 9.5a2.6 2.6 0 1 1 4.8 1.3c-.5.7-1.4 1.2-2 1.6-.7.4-1.1.9-1.1 1.6" />
    <path d="M12 17.5h.01" />
  `,
  'circle-info': `
    <circle cx="12" cy="12" r="9" />
    <path d="M12 10.5v5" />
    <path d="M12 7.5h.01" />
  `,
  music: `
    <path d="M9 18V6.5L19 4v11" />
    <circle cx="6.5" cy="18" r="2.5" />
    <circle cx="16.5" cy="15" r="2.5" />
  `,
  'message-circle': `
    <path d="M7 18l-4 3V7a3 3 0 0 1 3-3h12a3 3 0 0 1 3 3v8a3 3 0 0 1-3 3H7Z" />
  `,
  'messages-square': `
    <path d="M6 5h11a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3H9l-5 4V8a3 3 0 0 1 2-3Z" />
    <path d="M8 10h8" />
    <path d="M8 14h5" />
  `,
  'book-open': `
    <path d="M12 7v14" />
    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H12V5H6.5A2.5 2.5 0 0 0 4 7.5Z" />
    <path d="M20 19.5A2.5 2.5 0 0 0 17.5 17H12V5h5.5A2.5 2.5 0 0 1 20 7.5Z" />
  `,
  signpost: `
    <path d="M12 4v17" />
    <path d="M13 5h6l-2 3 2 3h-6" />
    <path d="M11 12H5l2 3-2 3h6" />
  `,
  terminal: `
    <path d="M4.5 7.5 9 12l-4.5 4.5" />
    <path d="M11.5 17h8" />
  `,
  shield: `
    <path d="M12 21s7-3.5 7-9.5V5.5L12 3 5 5.5v6C5 17.5 12 21 12 21Z" />
  `,
  settings: `
    <path d="M12 3.75v2.5" />
    <path d="M12 17.75v2.5" />
    <path d="M4.75 12h2.5" />
    <path d="M16.75 12h2.5" />
    <path d="m6.87 6.87 1.76 1.76" />
    <path d="m15.37 15.37 1.76 1.76" />
    <path d="m17.13 6.87-1.76 1.76" />
    <path d="m8.63 15.37-1.76 1.76" />
    <circle cx="12" cy="12" r="3.25" />
  `
}

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
  '/faq-v2': 'circle-help',
  '/netease-api': 'music',
  '/v2': 'book-open'
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
