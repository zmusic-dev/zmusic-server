<template>
  <div class="external-links-section">
    <div class="external-links">
      <a href="https://modrinth.com/mod/zmusic-client" target="_blank" rel="noreferrer" class="external-link">
        <span class="link-icon icon-modrinth" v-html="siModrinth.svg"></span>
        <span>Modrinth</span>
      </a>
      <a href="https://www.curseforge.com/minecraft/mc-mods/zmusic-client" target="_blank" rel="noreferrer" class="external-link">
        <span class="link-icon icon-curseforge" v-html="siCurseforge.svg"></span>
        <span>CurseForge</span>
      </a>
    </div>
    <p class="mirror-note">{{ t.note }}</p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vitepress'
import { siModrinth, siCurseforge } from 'simple-icons'
import { getSiteLocale } from '../utils/locale'

const route = useRoute()

const i18n = {
  '/': {
    note: '其他平台发布更新会稍晚于 GitHub'
  },
  '/en/': {
    note: 'Updates on other platforms may be slightly delayed'
  },
  '/ja/': {
    note: '他のプラットフォームの更新は GitHub より少し遅れることがあります'
  }
} as const

const locale = computed(() => getSiteLocale(route.path))
const t = computed(() => i18n[locale.value])
</script>

<style scoped>
.external-links-section {
  margin: 1rem 0;
}

.mirror-note {
  font-size: 0.85rem;
  color: var(--vp-c-warning-1);
  margin-top: 1rem;
  padding: 0.5rem 0.75rem;
  background: var(--vp-c-warning-soft);
  border-radius: 6px;
  text-align: center;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}

.external-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.external-link {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  padding: 1rem 1.5rem;
  font-size: 1rem;
  color: var(--vp-c-text-2);
  text-decoration: none;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: transparent;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: all 0.2s;
}

.external-link:hover {
  color: var(--vp-c-brand-1);
  border-color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
}

.link-icon {
  width: 1.25rem;
  height: 1.25rem;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.link-icon :deep(svg) {
  width: 1.25rem;
  height: 1.25rem;
}

.icon-modrinth :deep(svg) {
  fill: #00AF5C;
}

.icon-curseforge :deep(svg) {
  fill: #F16436;
}

@media (max-width: 639px) {
  .external-links {
    flex-direction: column;
    align-items: stretch;
  }

  .external-link {
    justify-content: center;
  }
}
</style>
