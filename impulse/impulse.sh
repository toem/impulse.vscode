#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
EXTENSION_PATH="$( cd "$SCRIPT_DIR/.." && pwd )"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo_info() {
    if [ "$VERBOSE" = true ]; then
        echo -e "${GREEN}[INFO]${NC} $1"
    fi
}

echo_warning() {
    if [ "$VERBOSE" = true ]; then
        echo -e "${YELLOW}[WARN]${NC} $1"
    fi
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo_step() {
    if [ "$VERBOSE" = true ]; then
        echo -e "${BLUE}[STEP]${NC} $1"
    fi
}

# Usage function
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Start the Impulse backend server."
    echo ""
    echo "Options:"
    echo "  --port <port>         Port number (default: 0 = auto-select)"
    echo "  --debug               Enable Java debugger on port 8888"
    echo "  --debugger-port <port> Java debugger port (default: 8888)"
    echo "  --java <path>         Path to Java executable (default: java)"
    echo "  --mcp                 Enable MCP support (default: disabled)"
    echo "  --frontend            Enable frontend mode (default: disabled)"
    echo "  --bundles <path>      Path to bundles directory to scan for JAR files"
    echo "  --expr <expression>   JX expression or JX script path"
    echo "  --verbose             Enable verbose output"
    echo "  --help                Show this help message"
    echo ""
    echo "Environment variables:"
    echo "  JAVA_CMD              Path to Java executable"
    echo "  PORT                  Port number"
    echo "  ENABLE_DEBUGGER       Enable debugger (true/false)"
    echo "  DEBUGGER_PORT         Java debugger port"
    exit 1
}

# Configuration defaults
JAVA_CMD="${JAVA_CMD:-java}"
PORT="${PORT:-0}"
ENABLE_DEBUGGER="${ENABLE_DEBUGGER:-false}"
DEBUGGER_PORT="${DEBUGGER_PORT:-8888}"
ENABLE_MCP=false
ENABLE_FRONTEND=false
BUNDLES_PATHS=()
EXPR=""
VERBOSE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --port)
            PORT="$2"
            shift 2
            ;;
        --debug)
            ENABLE_DEBUGGER=true
            shift
            ;;
        --debugger-port)
            DEBUGGER_PORT="$2"
            shift 2
            ;;
        --java)
            JAVA_CMD="$2"
            shift 2
            ;;
        --mcp)
            ENABLE_MCP=true
            shift
            ;;
        --frontend)
            ENABLE_FRONTEND=true
            shift
            ;;
        --bundles)
            BUNDLES_PATHS+=("$2")
            shift 2
            ;;
        --expr)
            EXPR="$2"
            shift 2
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        --help)
            usage
            ;;
        *)
            echo_error "Unknown option: $1"
            usage
            ;;
    esac
done

# Helper function for verbose output
verbose_echo() {
    if [ "$VERBOSE" = true ]; then
        echo "$@"
    fi
}

echo_step "Starting impulse"

# Find OSGi jar
OSGI_JAR="$SCRIPT_DIR/plugins/org.eclipse.osgi_3.16.200.v20210226-1447.jar"

if [ ! -f "$OSGI_JAR" ]; then
    echo_error "OSGi jar not found at $OSGI_JAR"
    exit 1
fi
echo_info "Found OSGi jar: $OSGI_JAR"

# Collect bundles
BUNDLES=""
if [ ${#BUNDLES_PATHS[@]} -gt 0 ]; then
    echo_step "Collecting bundles"
    BUNDLE_INDEX=1
    
    for BUNDLES_PATH in "${BUNDLES_PATHS[@]}"; do
        if [ ! -d "$BUNDLES_PATH" ]; then
            echo_error "Bundles directory not found: $BUNDLES_PATH"
            exit 1
        fi
        
        verbose_echo "Scanning directory: $BUNDLES_PATH"
        for bundle in "$BUNDLES_PATH"/*.jar; do
            if [ -f "$bundle" ]; then
                BUNDLES="$BUNDLES -Dbundle.$BUNDLE_INDEX=\"file://$bundle\""
                verbose_echo "  Added bundle $BUNDLE_INDEX: $(basename "$bundle")"
                BUNDLE_INDEX=$((BUNDLE_INDEX + 1))
            fi
        done
    done

    echo_info "Collected $((BUNDLE_INDEX - 1)) bundles from ${#BUNDLES_PATHS[@]} director$([ ${#BUNDLES_PATHS[@]} -eq 1 ] && echo "y" || echo "ies")"
else
    echo_info "No bundles directory specified"
fi

# Build Java command
echo_step "Configuring Java options"
JAVA_OPTS=""

if [ "$ENABLE_FRONTEND" = true ]; then
    JAVA_OPTS="$JAVA_OPTS -Dfrontend=true"
    echo_info "Frontend mode: enabled"
else
    echo_info "Frontend mode: disabled"
fi

if [ "$ENABLE_MCP" = true ]; then
    JAVA_OPTS="$JAVA_OPTS -Dmcp=true"
    echo_info "MCP support: enabled"
else
    echo_info "MCP support: disabled"
fi

if [ -n "$EXPR" ]; then
    JAVA_OPTS="$JAVA_OPTS -Dexpr=\"$EXPR\""
    echo_info "Expression: $EXPR"
fi

if [ "$PORT" -gt 0 ]; then
    JAVA_OPTS="$JAVA_OPTS -Dport=$PORT"
    echo_info "Port: $PORT (specified)"
else
    echo_info "Port: auto-select"
fi

if [ "$ENABLE_DEBUGGER" = true ]; then
    JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:$DEBUGGER_PORT,server=y,suspend=n"
    echo_info "Java debugger: enabled on port $DEBUGGER_PORT"
else
    echo_info "Java debugger: disabled"
fi

# Display configuration summary
echo_step "Configuration summary"
echo_info "Java executable: $JAVA_CMD"
echo_info "Working directory: $SCRIPT_DIR"
echo_info "OSGi jar: $(basename "$OSGI_JAR")"

if [ "$VERBOSE" = true ]; then
    echo_info "Full command:"
    echo "  $JAVA_CMD $JAVA_OPTS $BUNDLES -jar \"$OSGI_JAR\""
fi

# Execute
echo_step "Starting server"
cd "$SCRIPT_DIR"
eval "$JAVA_CMD $JAVA_OPTS $BUNDLES -jar \"$OSGI_JAR\""