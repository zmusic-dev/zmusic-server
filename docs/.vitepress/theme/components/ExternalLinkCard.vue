<template>
  <div class="external-links-section">
    <div class="external-links">
      <a
        v-for="link in links"
        :key="link.url"
        :href="link.url"
        target="_blank"
        rel="noreferrer"
        class="external-link"
      >
        <span
          class="link-icon"
          :class="`icon-${link.icon}`"
          v-html="link.svg"
        />
        <span>{{ link.label }}</span>
      </a>
    </div>
    <p v-if="note" class="mirror-note">{{ note }}</p>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  links: Array<{
    url: string
    label: string
    icon: string
    svg: string
  }>
  note?: string
}>()
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

/* Brand colors */
.icon-codeberg :deep(svg) { fill: #2185D0; }
.icon-gitee :deep(svg) { fill: #C71D23; }
.icon-spigotmc :deep(svg) { fill: #ED8106; }
.icon-modrinth :deep(svg) { fill: #00AF5C; }
.icon-curseforge :deep(svg) { fill: #F16436; }

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
