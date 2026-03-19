import type { Theme } from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import { h } from 'vue'
import DownloadTable from './components/DownloadTable.vue'
import DocPageHeading from './components/DocPageHeading.vue'
import HomeHeroScene from './components/HomeHeroScene.vue'
import NeteaseApiTable from './components/NeteaseApiTable.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  Layout: () =>
    h(DefaultTheme.Layout, null, {
      'doc-before': () => h(DocPageHeading),
      'home-hero-image': () => h(HomeHeroScene)
    }),
  enhanceApp({ app }) {
    app.component('DownloadTable', DownloadTable)
    app.component('NeteaseApiTable', NeteaseApiTable)
  }
} satisfies Theme
