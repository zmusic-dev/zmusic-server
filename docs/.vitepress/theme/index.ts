import type { Theme } from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import { h } from 'vue'
import DocPageHeading from './components/DocPageHeading.vue'
import DownloadTable from './components/DownloadTable.vue'
import GiscusComments from './components/GiscusComments.vue'
import HomeHeroScene from './components/HomeHeroScene.vue'
import NeteaseApiTable from './components/NeteaseApiTable.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  Layout: () =>
    h(DefaultTheme.Layout, null, {
      'doc-before': () => h(DocPageHeading),
      'doc-after': () => h(GiscusComments),
      'home-hero-image': () => h(HomeHeroScene)
    }),
  enhanceApp({ app }) {
    app.component('DownloadTable', DownloadTable)
    app.component('NeteaseApiTable', NeteaseApiTable)
  }
} satisfies Theme
