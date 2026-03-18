export type SiteLocale = '/' | '/en/' | '/zh-tw/' | '/ja/'

export function getSiteLocale(path: string): SiteLocale {
  if (path.startsWith('/en/')) return '/en/'
  if (path.startsWith('/zh-tw/')) return '/zh-tw/'
  if (path.startsWith('/ja/')) return '/ja/'
  return '/'
}
