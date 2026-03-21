export type NeteaseApiType = 'standard' | 'enhanced'

export interface NeteaseApiData {
  link: string
  type: NeteaseApiType
  location: Record<string, string>
  provider: {
    name: string
    link: string
  }
}

const neteaseApiList: NeteaseApiData[] = [
  // 标准版 (NeteaseCloudMusicApi)
  {
    link: 'https://ncm.zhenxin.me',
    type: 'standard',
    location: {
      '/': '上海',
      '/en/': 'Shanghai',
      '/zh-tw/': '上海',
      '/ja/': '上海'
    },
    provider: {
      name: '真心',
      link: 'https://github.com/RealHeart'
    }
  },
  {
    link: 'https://zm.i9mr.com',
    type: 'standard',
    location: {
      '/': '扬州',
      '/en/': 'Yangzhou',
      '/zh-tw/': '揚州',
      '/ja/': '揚州'
    },
    provider: {
      name: '墨染云',
      link: 'https://i9mr.com'
    }
  },
  {
    link: 'https://music.mcseekeri.com',
    type: 'standard',
    location: {
      '/': '美国',
      '/en/': 'USA',
      '/zh-tw/': '美國',
      '/ja/': 'アメリカ'
    },
    provider: {
      name: 'MCSeekeri',
      link: 'https://github.com/MCSeekeri'
    }
  },
  {
    link: 'https://zm.wwoyun.cn',
    type: 'standard',
    location: {
      '/': '宁波',
      '/en/': 'Ningbo',
      '/zh-tw/': '寧波',
      '/ja/': '寧波'
    },
    provider: {
      name: '蓝钦',
      link: 'https://github.com/LanQin996'
    }
  },
  // 增强版 (NeteaseCloudMusicApi-Enhanced)
  {
    link: 'https://ncme.zhenxin.me',
    type: 'enhanced',
    location: {
      '/': '上海',
      '/en/': 'Shanghai',
      '/zh-tw/': '上海',
      '/ja/': '上海'
    },
    provider: {
      name: '真心',
      link: 'https://github.com/RealHeart'
    }
  }
]

export default neteaseApiList
