<template>
  <ExternalLinkCard :links="links" :note="t.note" />
</template>

<script lang="ts" setup>
import { siModrinth, siCurseforge } from 'simple-icons'
import ExternalLinkCard from './ExternalLinkCard.vue'
import { useI18n } from '../utils/i18n'

const t = useI18n()

const links = [
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
