#!/bin/bash

# Function to detect compiler type based on file content heuristics
detect_compiler() {
    local file="$1"
    
    # Check if file has GNU Assembler (GAS) characteristics
    if grep -q -E '^\s*\.(global|globl|intel_syntax|text|data|section)' "$file" || grep -q -E '%\b(rax|rbx|rcx|rdx|rsi|rdi|rbp|rsp|r8|r9|r10|r11|r12|r13|r14|r15|eax|ebx|ecx|edx|esi|edi|ebp|esp|ax|bx|cx|dx)\b' "$file"; then
        echo "gas"
        return
    fi
    
    # Check if file has NASM characteristics
    if grep -q -E '^\s*(bits|default rel|%define|%include|global\s+\w+)' "$file"; then
        echo "nasm"
        return
    fi
    
    # Fallback to nasm if it is installed, otherwise gas
    if command -v nasm >/dev/null 2>&1; then
        echo "nasm"
    elif command -v as >/dev/null 2>&1; then
        echo "gas"
    else
        echo "unknown"
    fi
}

# Monitoring function that tracks inactivity
run_monitor() {
    local executable="$1"
    local log_file="$2"
    local timeout="$3"
    local obj_file="$4"
    local foreground="$5"
    
    # Run the executable in background
    ./"$executable" > "$log_file" 2>&1 &
    local pid=$!
    
    local last_size=0
    local last_activity
    last_activity=$(date +%s)
    
    while kill -0 "$pid" 2>/dev/null; do
        sleep 0.2
        local current_size
        current_size=$(stat -c%s "$log_file" 2>/dev/null || echo 0)
        local current_time
        current_time=$(date +%s)
        
        if [ "$current_size" -gt "$last_size" ]; then
            # In foreground mode, print the new output to the console in real-time
            if [ "$foreground" = "true" ]; then
                tail -c +"$((last_size + 1))" "$log_file"
            fi
            last_size=$current_size
            last_activity=$current_time
        else
            local elapsed=$((current_time - last_activity))
            if [ "$elapsed" -ge "$timeout" ]; then
                if [ "$foreground" = "true" ]; then
                    echo -e "\n[MONITOR] Program silent/unresponsive for $timeout seconds. Terminating process $pid..."
                else
                    echo -e "\n[MONITOR] Program silent/unresponsive for $timeout seconds. Terminating process $pid..." >> "$log_file"
                fi
                kill -9 "$pid" 2>/dev/null
                wait "$pid" 2>/dev/null
                break
            fi
        fi
    done
    
    # Wait for process if it exited on its own to collect exit code
    wait "$pid" 2>/dev/null
    local exit_code=$?
    
    if [ "$foreground" = "true" ]; then
        # Print any final leftover output
        local final_size
        final_size=$(stat -c%s "$log_file" 2>/dev/null || echo 0)
        if [ "$final_size" -gt "$last_size" ]; then
            tail -c +"$((last_size + 1))" "$log_file"
        fi
        echo "[MONITOR] Process finished. Exit code: $exit_code"
    else
        echo "[MONITOR] Process finished. Exit code: $exit_code" >> "$log_file"
    fi
    
    # Clean up executable and object file
    rm -f "$executable" "$obj_file"
}

# Handle internal monitor call from daemon mode
if [ "$1" = "--monitor-internal" ]; then
    run_monitor "$2" "$3" "$4" "$5" "$6"
    exit 0
fi

# Print usage
print_usage() {
    echo "Usage: $0 [options] <file.asm>"
    echo "Options:"
    echo "  -d, --daemon     Run the program and monitor loop in daemon mode (background)"
    echo "  -t, --timeout S  Timeout in seconds for inactivity (default: 15)"
    echo "  -h, --help       Show this help message"
}

# Parse command line options
DAEMON_MODE=false
TIMEOUT=15
ASM_FILE=""

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -d|--daemon)
            DAEMON_MODE=true
            shift
            ;;
        -t|--timeout)
            TIMEOUT="$2"
            shift 2
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        -*)
            echo "Unknown option: $1"
            print_usage
            exit 1
            ;;
        *)
            ASM_FILE="$1"
            shift
            ;;
    esac
done

if [ -z "$ASM_FILE" ]; then
    echo "Error: No assembly file specified."
    print_usage
    exit 1
fi

if [ ! -f "$ASM_FILE" ]; then
    echo "Error: File '$ASM_FILE' does not exist."
    exit 1
fi

# Extract names and paths
ASM_NAME="${ASM_FILE%.*}"
ASM_OUTPUT="${ASM_NAME}.o"
ASM_EXECUTABLE="${ASM_NAME}.out"
LOG_FILE="${ASM_NAME}.log"

# Compile and link
COMPILER=$(detect_compiler "$ASM_FILE")
echo "Detected compiler/assembler: $COMPILER"

if [ "$COMPILER" = "nasm" ]; then
    if ! command -v nasm >/dev/null 2>&1; then
        echo "Error: nasm is not installed."
        exit 1
    fi
    echo "Assembling using nasm..."
    if ! nasm -f elf64 "$ASM_FILE" -o "$ASM_OUTPUT"; then
        echo "Error: Assembly compilation failed."
        exit 1
    fi
elif [ "$COMPILER" = "gas" ]; then
    if ! command -v as >/dev/null 2>&1; then
        echo "Error: 'as' (GNU Assembler) is not installed."
        exit 1
    fi
    echo "Assembling using GNU Assembler (as)..."
    if ! as --64 "$ASM_FILE" -o "$ASM_OUTPUT"; then
        echo "Error: Assembly compilation failed."
        exit 1
    fi
else
    echo "Error: Could not determine assembler type for $ASM_FILE."
    exit 1
fi

# Linking
echo "Linking..."
if ld "$ASM_OUTPUT" -o "$ASM_EXECUTABLE" 2>/dev/null; then
    echo "Linking successful: $ASM_EXECUTABLE"
elif gcc -no-pie "$ASM_OUTPUT" -o "$ASM_EXECUTABLE" 2>/dev/null; then
    echo "Linking successful (via gcc): $ASM_EXECUTABLE"
else
    echo "Error: Linking failed."
    ld "$ASM_OUTPUT" -o "$ASM_EXECUTABLE" # Run again to show compilation error output
    rm -f "$ASM_OUTPUT"
    exit 1
fi

# Make sure log file starts fresh
> "$LOG_FILE"

# Start the monitor
if [ "$DAEMON_MODE" = "true" ]; then
    # Run the script itself in the background with the internal monitor flag
    # Resolve absolute path of $0 to ensure daemon can execute it correctly
    SCRIPT_PATH=$(realpath "$0")
    nohup "$SCRIPT_PATH" --monitor-internal "$ASM_EXECUTABLE" "$LOG_FILE" "$TIMEOUT" "$ASM_OUTPUT" "false" >/dev/null 2>&1 &
    disown
    DAEMON_PID=$!
    echo "--------------------------------------------------------"
    echo "Program running in daemon mode (PID: $DAEMON_PID)."
    echo "Monitor is checking inactivity (timeout: $TIMEOUT seconds)."
    echo "Log output: $LOG_FILE"
    echo "--------------------------------------------------------"
else
    echo "--------------------------------------------------------"
    echo "Starting program in foreground mode..."
    echo "Press Ctrl+C to terminate early."
    echo "Inactivity timeout is set to $TIMEOUT seconds."
    echo "--------------------------------------------------------"
    run_monitor "$ASM_EXECUTABLE" "$LOG_FILE" "$TIMEOUT" "$ASM_OUTPUT" "true"
    # Clean up the log file in foreground mode
    rm -f "$LOG_FILE"
fi