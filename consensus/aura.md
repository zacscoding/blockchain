# Aura (ethereum parity client)  

1. Define step  

```
s = UNIX time / t, where t is steps of duration    
```  

2. Primary node  

```
idx = s mod n
```  

3. Difficulty  

```
U128:max_value() + parent_step - current_step + current_empty_steps  
```  

5. Finality   
; todo

6. Examples

00. Ref  

- https://wiki.parity.io/Pluggable-Consensus#validator-engines
; aura validators  
- https://wiki.parity.io/Pluggable-Consensus.html#aura
- https://wiki.parity.io/Aura.html
