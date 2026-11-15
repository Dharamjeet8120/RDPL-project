// Editable table used for teacher assignments and curriculum items.
export default function RowEditor({ rows, columns, onChange, newRow, addLabel = 'Add row' }) {
  const update = (index, key, value) => onChange(rows.map((r, i) => (i === index ? { ...r, [key]: value } : r)))
  const remove = (index) => onChange(rows.filter((_, i) => i !== index))

  return (
    <div className="editor">
      <div className="table-wrap">
        <table className="inner">
          <thead>
            <tr>
              {columns.map((c) => (
                <th key={c.key}>{c.label}</th>
              ))}
              <th />
            </tr>
          </thead>
          <tbody>
            {rows.map((row, i) => (
              <tr key={i}>
                {columns.map((c) => (
                  <td key={c.key}>
                    {c.options ? (
                      <select aria-label={c.label} value={row[c.key] ?? ''} onChange={(e) => update(i, c.key, e.target.value)}>
                        {c.options.map((o) => (
                          <option key={o}>{o}</option>
                        ))}
                      </select>
                    ) : (
                      <input
                        aria-label={c.label}
                        type={c.number ? 'number' : 'text'}
                        min={c.number ? 0 : undefined}
                        list={c.list}
                        value={row[c.key] ?? ''}
                        onChange={(e) => update(i, c.key, c.number ? Number(e.target.value) : e.target.value)}
                      />
                    )}
                  </td>
                ))}
                <td>
                  <button type="button" className="btn btn-small btn-danger" onClick={() => remove(i)}>
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <button type="button" className="btn btn-small" onClick={() => onChange([...rows, newRow()])}>
        {addLabel}
      </button>
    </div>
  )
}
