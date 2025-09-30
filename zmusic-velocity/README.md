# ZMusic Velocity Support

This module provides Velocity proxy server support for ZMusic, enabling cross-server music synchronization on Velocity-based server networks.

## Features

- ✅ Cross-server music playbook synchronization
- ✅ Player join event handling
- ✅ Command system with tab completion
- ✅ Plugin messaging for mod communication
- ✅ Adventure API text components
- ✅ bStats metrics integration
- ✅ Full compatibility with existing ZMusic features

## Installation

1. Build the ZMusic project
2. Place the generated `zmusic-velocity-<version>.jar` in your Velocity `plugins` folder
3. Restart your Velocity proxy
4. Configure ZMusic as needed using `/zm` commands

## Configuration

The Velocity plugin uses the same configuration system as the BungeeCord version:
- Plugin channels: `zmusic:channel`, `allmusic:channel`, `AudioBuffer`
- Commands: `/zm`, `/zmusic`, `/music`
- Permissions: Standard ZMusic permission nodes

## Implementation Details

### Core Components

- **ZMusicVelocity**: Main plugin class handling initialization and shutdown
- **LogVelocity**: Adventure-compatible logging implementation
- **MessageVelocity**: Modern text component messaging system
- **PlayerVelocity**: Player management and permission handling
- **RunTaskVelocity**: Async task execution
- **SendVelocity**: Plugin messaging for mod communication
- **CmdVelocity**: Command handling with tab completion
- **EventVelocity**: Player join event processing

### Differences from BungeeCord Implementation

1. **Adventure API**: Uses modern Adventure text components instead of legacy ChatColor
2. **Plugin System**: Uses Velocity's annotation-based plugin system
3. **Event Handling**: Uses Velocity's event subscription model
4. **Command System**: Implements SimpleCommand interface with async tab completion

## Development

The Velocity implementation follows the same patterns as the existing BungeeCord implementation, ensuring consistency and maintainability across the codebase.

### Dependencies

- Velocity API 3.3.0-SNAPSHOT
- Adventure API (included with Velocity)
- bStats Velocity 3.1.0
- Standard ZMusic core dependencies

## Compatibility

This implementation is designed to be a drop-in replacement for the BungeeCord version when using Velocity proxy servers. All existing ZMusic features and configurations should work without modification.