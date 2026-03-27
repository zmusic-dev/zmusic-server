<template>
  <ExternalLinkCard :links="links" :note="t.note" />
</template>

<script lang="ts" setup>
import { siModrinth, siCurseforge, siCodeberg } from 'simple-icons'
import ExternalLinkCard from './ExternalLinkCard.vue'
import { useI18n } from '../utils/i18n'
import cnbSvg from '../assets/cnb.svg?raw'

const t = useI18n()

const links = [
  {
    url: 'https://codeberg.org/zmusic-dev/zmusic-client/releases',
    label: 'Codeberg',
    icon: 'codeberg',
    svg: siCodeberg.svg
  },
  {
    url: 'https://cnb.cool/zmusic-dev/zmusic-client/-/releases',
    label: 'CNB',
    icon: 'cnb',
    svg: cnbSvg
  },
  {
    url: 'https://modrinth.com/mod/zmusic-client',
    label: 'Modrinth',
    icon: 'modrinth',
    svg: siModrinth.svg
  },
  {
    url: 'https://www.curseforge.com/minecraft/mc-mods/zmusic-client',
    label: 'CurseForge',
    icon: 'curseforge',
    svg: siCurseforge.svg
  }
]
</script>
