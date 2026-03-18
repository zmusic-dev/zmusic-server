import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vitepress'
import type { DefaultTheme } from 'vitepress'

type LocaleKey = 'root' | 'en' | 'zh-tw' | 'ja'

interface LocaleNavigationCopy {
  home: string
  guide: string
  faq: string
  community: string
  communityLink: string
}

interface LocaleSidebarCopy {
  guide: string
  introduction: string
  gettingStarted: string
  commands: string
  permissions: string
  config: string
  faq: string
  neteaseApi: string
}

const localeBases: Record<LocaleKey, string> = {
  root: '/',
  en: '/en/',
  'zh-tw': '/zh-tw/',
  ja: '/ja/'
}

const localeNavigationCopy: Record<LocaleKey, LocaleNavigationCopy> = {
  root: {
    home: '首页',
    guide: '指南',
    faq: '常见问题',
    community: 'QQ群',
    communityLink: 'https://jq.qq.com/?_wv=1027&k=9Xs1RMt5'
  },
  en: {
    home: 'Home',
    guide: 'Guide',
    faq: 'FAQ',
    community: 'Discord',
    communityLink: 'https://discord.gg/twQgJNufYn'
  },
  'zh-tw': {
    home: '首頁',
    guide: '指南',
    faq: '常見問題',
    community: 'Discord',
    communityLink: 'https://discord.gg/twQgJNufYn'
  },
  ja: {
    home: 'ホーム',
    guide: 'ガイド',
    faq: 'よくある質問',
    community: 'Discord',
    communityLink: 'https://discord.gg/twQgJNufYn'
  }
}

const localeSidebarCopy: Record<LocaleKey, LocaleSidebarCopy> = {
  root: {
    guide: '指南',
    introduction: '介绍',
    gettingStarted: '快速开始',
    commands: '命令',
    permissions: '权限',
    config: '配置文件',
    faq: '常见问题',
    neteaseApi: '网易云音乐 API'
  },
  en: {
    guide: 'Guide',
    introduction: 'Introduction',
    gettingStarted: 'Getting Started',
    commands: 'Commands',
    permissions: 'Permissions',
    config: 'Configuration',
    faq: 'FAQ',
    neteaseApi: 'Netease Cloud Music API'
  },
  'zh-tw': {
    guide: '指南',
    introduction: '介紹',
    gettingStarted: '快速開始',
    commands: '命令',
    permissions: '權限',
    config: '設定檔',
    faq: '常見問題',
    neteaseApi: '網易雲音樂 API'
  },
  ja: {
    guide: 'ガイド',
    introduction: '紹介',
    gettingStarted: 'はじめに',
    commands: 'コマンド',
    permissions: '権限',
    config: '設定ファイル',
    faq: 'よくある質問',
    neteaseApi: 'NetEase Cloud Music API'
  }
}

function withLocaleBase(locale: LocaleKey, path = '') {
  return path ? `${localeBases[locale]}${path}` : localeBases[locale]
}

function createNav(locale: LocaleKey): DefaultTheme.NavItem[] {
  const copy = localeNavigationCopy[locale]

  return [
    { text: copy.home, link: withLocaleBase(locale) },
    {
      text: copy.guide,
      link: withLocaleBase(locale, 'guide/'),
      activeMatch: `^${withLocaleBase(locale, 'guide/')}`
    },
    { text: copy.faq, link: withLocaleBase(locale, 'faq') },
    { text: copy.community, link: copy.communityLink }
  ]
}

function createSidebar(locale: LocaleKey): DefaultTheme.SidebarItem[] {
  const copy = localeSidebarCopy[locale]

  return [
    {
      text: copy.guide,
      link: withLocaleBase(locale, 'guide/'),
      items: [
        { text: copy.introduction, link: withLocaleBase(locale, 'guide/introduction') },
        { text: copy.gettingStarted, link: withLocaleBase(locale, 'guide/getting-started') },
        { text: copy.commands, link: withLocaleBase(locale, 'guide/commands') },
        { text: copy.permissions, link: withLocaleBase(locale, 'guide/permissions') },
        { text: copy.config, link: withLocaleBase(locale, 'guide/config') }
      ]
    },
    { text: copy.faq, link: withLocaleBase(locale, 'faq') },
    { text: copy.neteaseApi, link: withLocaleBase(locale, 'netease-api') }
  ]
}

const zhNav = createNav('root')
const enNav = createNav('en')
const zhTwNav = createNav('zh-tw')
const jaNav = createNav('ja')

const zhSidebar = createSidebar('root')
const enSidebar = createSidebar('en')
const zhTwSidebar = createSidebar('zh-tw')
const jaSidebar = createSidebar('ja')

const siteFooter: DefaultTheme.Footer = {
  message:
    '<a href="https://beian.miit.gov.cn" target="_blank" rel="noreferrer">辽ICP备19016520号-4</a>',
  copyright:
    '&copy;2026 <a href="https://zhenxin.me" target="_blank" rel="noreferrer">ZhenXin</a> All Rights Reserved.'
}

export default defineConfig({
  lang: 'zh-CN',
  title: 'ZMusic',
  description: 'ZMusic - 多功能、易上手的 Minecraft 跨平台点歌插件',
  lastUpdated: true,
  vite: {
    resolve: {
      alias: [
        {
          find: /^.*\/VPNavBarMenuLink\.vue$/,
          replacement: fileURLToPath(
            new URL('./theme/components/VPNavBarMenuLink.vue', import.meta.url)
          )
        },
        {
          find: /^.*\/VPMenuLink\.vue$/,
          replacement: fileURLToPath(new URL('./theme/components/VPMenuLink.vue', import.meta.url))
        },
        {
          find: /^.*\/VPSidebarItem\.vue$/,
          replacement: fileURLToPath(
            new URL('./theme/components/VPSidebarItem.vue', import.meta.url)
          )
        }
      ]
    }
  },
  head: [
    ['meta', { name: 'viewport', content: 'width=device-width,initial-scale=1,user-scalable=no' }],
    ['script', { src: 'https://cdn.zhenxin.me/static/js/autoGray.js' }],
    [
      'script',
      {
        defer: '',
        src: 'https://umami.zhenxin.me/script.js',
        'data-website-id': 'c5a252ad-badb-488e-80c8-81c366119027'
      }
    ],
    ['meta', { name: 'author', content: 'ZhenXin' }],
    [
      'meta',
      {
        name: 'keywords',
        content:
          'ZMusic, Minecraft, Plugin, Music, 点歌插件, 网易云音乐, BungeeCord, Spigot, Velocity'
      }
    ]
  ],
  sitemap: {
    hostname: 'https://zmusic.zhenxin.me'
  },
  themeConfig: {
    socialLinks: [
      { icon: 'github', link: 'https://github.com/zmusic-dev/zmusic-server' }
    ],
    search: {
      provider: 'local',
      options: {
        locales: {
          root: {
            translations: {
              button: {
                buttonText: '搜索文档',
                buttonAriaLabel: '搜索文档'
              },
              modal: {
                noResultsText: '未找到相关结果',
                resetButtonTitle: '清除查询条件',
                footer: {
                  selectText: '选择',
                  navigateText: '切换',
                  closeText: '关闭'
                }
              }
            }
          },
          en: {
            translations: {
              button: {
                buttonText: 'Search docs',
                buttonAriaLabel: 'Search docs'
              }
            }
          },
          'zh-tw': {
            translations: {
              button: {
                buttonText: '搜尋文件',
                buttonAriaLabel: '搜尋文件'
              },
              modal: {
                noResultsText: '找不到相關結果',
                resetButtonTitle: '清除搜尋條件',
                footer: {
                  selectText: '選擇',
                  navigateText: '切換',
                  closeText: '關閉'
                }
              }
            }
          },
          ja: {
            translations: {
              button: {
                buttonText: 'ドキュメントを検索',
                buttonAriaLabel: 'ドキュメントを検索'
              }
            }
          }
        }
      }
    },
    i18nRouting: true
  },
  locales: {
    root: {
      label: '简体中文',
      link: '/',
      lang: 'zh-CN',
      title: 'ZMusic 使用文档',
      description: 'ZMusic - 多功能、易上手的 Minecraft 跨平台点歌插件',
      themeConfig: {
        logoLink: withLocaleBase('root'),
        nav: zhNav,
        sidebar: zhSidebar,
        outline: {
          level: [2, 3],
          label: '目录'
        },
        docFooter: {
          prev: '上一页',
          next: '下一页'
        },
        lastUpdated: {
          text: '最后更新于'
        },
        returnToTopLabel: '返回顶部',
        sidebarMenuLabel: '菜单',
        darkModeSwitchLabel: '主题',
        langMenuLabel: '切换语言',
        footer: siteFooter
      }
    },
    en: {
      label: 'English',
      link: '/en/',
      lang: 'en-US',
      title: 'ZMusic Docs',
      description: 'ZMusic - Versatile, easy-to-use cross-platform music player plugin for Minecraft',
      themeConfig: {
        logoLink: withLocaleBase('en'),
        nav: enNav,
        sidebar: enSidebar,
        outline: {
          level: [2, 3],
          label: 'On this page'
        },
        docFooter: {
          prev: 'Previous page',
          next: 'Next page'
        },
        lastUpdated: {
          text: 'Last updated'
        },
        returnToTopLabel: 'Return to top',
        sidebarMenuLabel: 'Menu',
        darkModeSwitchLabel: 'Theme',
        langMenuLabel: 'Change language',
        footer: siteFooter
      }
    },
    'zh-tw': {
      label: '繁體中文',
      link: '/zh-tw/',
      lang: 'zh-TW',
      title: 'ZMusic 使用文件',
      description: 'ZMusic - 多功能、易上手的 Minecraft 跨平台點歌插件',
      themeConfig: {
        logoLink: withLocaleBase('zh-tw'),
        nav: zhTwNav,
        sidebar: zhTwSidebar,
        outline: {
          level: [2, 3],
          label: '目錄'
        },
        docFooter: {
          prev: '上一頁',
          next: '下一頁'
        },
        lastUpdated: {
          text: '最後更新於'
        },
        returnToTopLabel: '返回頂部',
        sidebarMenuLabel: '選單',
        darkModeSwitchLabel: '主題',
        langMenuLabel: '切換語言',
        footer: siteFooter
      }
    },
    ja: {
      label: '日本語',
      link: '/ja/',
      lang: 'ja-JP',
      title: 'ZMusic ドキュメント',
      description: 'ZMusic - 多機能で使いやすい Minecraft クロスプラットフォーム音楽再生プラグイン',
      themeConfig: {
        logoLink: withLocaleBase('ja'),
        nav: jaNav,
        sidebar: jaSidebar,
        outline: {
          level: [2, 3],
          label: '目次'
        },
        docFooter: {
          prev: '前のページ',
          next: '次のページ'
        },
        lastUpdated: {
          text: '最終更新'
        },
        returnToTopLabel: 'トップに戻る',
        sidebarMenuLabel: 'メニュー',
        darkModeSwitchLabel: 'テーマ',
        langMenuLabel: '言語を切り替え',
        footer: siteFooter
      }
    }
  }
})
