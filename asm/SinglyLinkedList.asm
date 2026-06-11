; Singly Linked List Implementation in x86_64 Assembly
; Uses sys_brk for simple dynamic memory allocation.

bits 64
default rel

; Node: [ +0] value (8 bytes), [ +8] next (8 bytes pointer)
%define NODE_SIZE 16

; List: [ +0] head (8 bytes pointer), [ +8] tail (8 bytes pointer)
%define LIST_HEAD 0
%define LIST_TAIL 8
%define LIST_SIZE 16

section .data
    msg_arrow db " -> ", 0
    len_arrow equ $ - msg_arrow
    msg_null  db "NULL", 10, 0
    len_null  equ $ - msg_null
    msg_start db "List contents: ", 0
    len_start equ $ - msg_start

section .bss
    myList      resb LIST_SIZE
    buffer_int  resb 21
    current_brk resq 1

section .text
    global _start

_start:
    ; Initialize List (NULL head and tail)
    mov qword [myList + LIST_HEAD], 0
    mov qword [myList + LIST_TAIL], 0

    ; Get current program break (brk)
    mov rax, 12         ; sys_brk
    xor rdi, rdi
    syscall
    mov [current_brk], rax

    ; Test cases
    mov rdi, 10
    call list_push_back
    mov rdi, 20
    call list_push_back
    mov rdi, 5
    call list_push_front
    mov rdi, 30
    call list_push_back

    ; Print results
    mov rdi, msg_start
    mov rsi, len_start
    call _print_string
    call list_print

    ; Exit
    mov rax, 60
    xor rdi, rdi
    syscall

; list_push_back(rdi: value): Adds node to the end
list_push_back:
    push rbp
    mov rbp, rsp
    push rdi

    mov rdi, NODE_SIZE
    call _malloc
    mov rbx, rax

    pop rdi
    mov [rbx], rdi
    mov qword [rbx + 8], 0

    mov rdx, [myList + LIST_TAIL]
    test rdx, rdx
    jz .empty_list

    mov [rdx + 8], rbx
    mov [myList + LIST_TAIL], rbx
    jmp .done

.empty_list:
    mov [myList + LIST_HEAD], rbx
    mov [myList + LIST_TAIL], rbx

.done:
    pop rbp
    ret

; list_push_front(rdi: value): Adds node to the beginning
list_push_front:
    push rbp
    mov rbp, rsp
    push rdi

    mov rdi, NODE_SIZE
    call _malloc
    mov rbx, rax

    pop rdi
    mov [rbx], rdi
    
    mov rdx, [myList + LIST_HEAD]
    mov [rbx + 8], rdx
    mov [myList + LIST_HEAD], rbx

    mov rdx, [myList + LIST_TAIL]
    test rdx, rdx
    jnz .done
    mov [myList + LIST_TAIL], rbx

.done:
    pop rbp
    ret

; list_print(): Iterates and prints list values
list_print:
    push rbp
    mov rbp, rsp
    mov rbx, [myList + LIST_HEAD]

.loop:
    test rbx, rbx
    jz .end_list

    mov rax, [rbx]
    call _print_int_raw

    mov rdi, msg_arrow
    mov rsi, len_arrow
    call _print_string

    mov rbx, [rbx + 8]
    jmp .loop

.end_list:
    mov rdi, msg_null
    mov rsi, len_null
    call _print_string

    pop rbp
    ret

; _malloc(rdi: size) -> rax: address (via sys_brk)
_malloc:
    push rbx
    mov rax, [current_brk]
    mov rbx, rax
    
    add rax, rdi
    mov rdi, rax
    mov rax, 12
    syscall
    
    mov [current_brk], rax
    mov rax, rbx
    pop rbx
    ret

; _print_string(rdi: buffer, rsi: length): sys_write to stdout
_print_string:
    mov rdx, rsi
    mov rsi, rdi
    mov rdi, 1
    mov rax, 1
    syscall
    ret

; _print_int_raw(rax: value): Converts and prints integer without newline
_print_int_raw:
    push rbp
    mov rbp, rsp
    push rbx            ; Preserve RBX as it's used in list_print loop
    
    mov rbx, 10
    mov rcx, buffer_int
    add rcx, 20
    
.conv_loop:
    dec rcx
    xor rdx, rdx
    div rbx
    add dl, '0'
    mov [rcx], dl
    test rax, rax
    jnz .conv_loop

    mov rdi, rcx
    mov rsi, buffer_int
    add rsi, 20
    sub rsi, rcx
    call _print_string

    pop rbx             ; Restore RBX
    pop rbp
    ret
