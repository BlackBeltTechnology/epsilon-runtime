# Epsilon Development Notes

Important gotchas and pitfalls discovered while working with the Eclipse Epsilon framework in this runtime. Keep these in mind when writing or debugging Epsilon scripts.

## ETL Guard Inheritance

Guards defined on ETL rules are **not inherited** by child rules. If a parent rule has a guard, you must explicitly repeat that guard in every inherited rule — Epsilon will not apply it automatically.

```etl
// Parent rule with guard
rule BaseEntityToTable
    transform s : SRC!Entity
    to t : TGT!Table {
    guard: s.name <> "Abstract"
    ...
}

// Child rule MUST repeat the guard — it is NOT inherited
rule SpecialEntityToTable
    transform s : SRC!Entity
    to t : TGT!SpecialTable
    extends BaseEntityToTable {
    guard: s.name <> "Abstract"   // Required! Not inherited from parent.
    ...
}
```

## Model Caching During EGL/EOL Generation

When an EMF model is used by EOL scripts during code generation (EGL/EGX), and the return values of EOL operations depend on runtime state (i.e., they are **not constants**), the model's `cache` flag **must be set to `false`**.

If caching is left enabled (the default), Epsilon may return stale cached results from previous operation invocations, leading to subtle and hard-to-debug generation errors.

```java
emfModelContextBuilder()
    .name("MODEL")
    .emf("urn:my-model.xmi")
    .cached(false)   // Required when EOL operations return non-constant values
    .build()
```

## Equivalent Calls in ETL Guards

The `equivalent()` function is **not supported** inside the guard of an ETL rule. Calling `equivalent()` in a guard will fail because the equivalent objects have not yet been created at guard evaluation time.

Move `equivalent()` calls into the rule body instead.
