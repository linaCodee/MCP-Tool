import {
  Alert, Box, Button, Chip, CircularProgress, Grid, List, ListItemButton, ListItemText,
  Paper, TextField, Typography
} from '@mui/material';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import { useMemo, useState } from 'react';
import useMcpTools from '../../../../hooks/useMcpTools.ts';
import type { InvokeToolResponse } from '../../../../api/types/mcp.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';

/**
 * The MCP playground: pick a tool the server exposes, inspect its JSON Schema,
 * provide arguments as JSON and invoke it — exactly what an MCP agent such as
 * the Vezilka host does. Fully provided. The provided `corpus_stats` tool works
 * out of the box; the others report "not implemented" until you build them.
 */
const McpPlaygroundPage = () => {
  const { tools, loading, invoking, invoke } = useMcpTools();
  const { showSnackbar } = useSnackbar();

  const [selected, setSelected] = useState<string | null>(null);
  const [args, setArgs] = useState<string>('{}');
  const [response, setResponse] = useState<InvokeToolResponse | null>(null);

  const selectedTool = useMemo(() => tools.find((t) => t.name === selected) ?? null, [tools, selected]);

  const prettySchema = useMemo(() => {
    if (!selectedTool) {
      return '';
    }
    try {
      return JSON.stringify(JSON.parse(selectedTool.inputSchema), null, 2);
    } catch {
      return selectedTool.inputSchema;
    }
  }, [selectedTool]);

  const handleSelect = (name: string) => {
    setSelected(name);
    setArgs('{}');
    setResponse(null);
  };

  const handleInvoke = async () => {
    if (!selectedTool) {
      return;
    }
    let parsed: Record<string, unknown>;
    try {
      parsed = args.trim() ? JSON.parse(args) : {};
    } catch {
      showSnackbar('Arguments must be valid JSON.', 'error');
      return;
    }
    const result = await invoke(selectedTool.name, parsed);
    if (result) {
      setResponse(result);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress/>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant='h5' gutterBottom>MCP Playground</Typography>
      <Typography variant='body2' color='text.secondary' sx={{ mb: 2 }}>
        These are the tools this server exposes over MCP. Selecting one shows the
        JSON Schema an agent would see; invoking it runs the real tool.
      </Typography>
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Paper variant='outlined'>
            <List>
              {tools.map((tool) => (
                <ListItemButton
                  key={tool.name}
                  selected={tool.name === selected}
                  onClick={() => handleSelect(tool.name)}
                >
                  <ListItemText primary={tool.name} secondary={tool.description}/>
                </ListItemButton>
              ))}
              {tools.length === 0 && (
                <Box sx={{ p: 2 }}>
                  <Typography color='text.secondary'>No tools registered.</Typography>
                </Box>
              )}
            </List>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 8 }}>
          {selectedTool ? (
            <Paper variant='outlined' sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box>
                <Typography variant='h6'>{selectedTool.name}</Typography>
                <Typography variant='body2' color='text.secondary'>{selectedTool.description}</Typography>
              </Box>
              <Box>
                <Typography variant='subtitle2' gutterBottom>Input schema</Typography>
                <Box component='pre' sx={{ m: 0, p: 1, bgcolor: 'action.hover', borderRadius: 1, overflowX: 'auto', fontSize: 12 }}>
                  {prettySchema}
                </Box>
              </Box>
              <TextField
                label='Arguments (JSON)'
                value={args}
                onChange={(e) => setArgs(e.target.value)}
                multiline
                minRows={3}
                fullWidth
                slotProps={{ htmlInput: { style: { fontFamily: 'monospace' } } }}
              />
              <Box>
                <Button
                  variant='contained'
                  startIcon={<PlayArrowIcon/>}
                  onClick={handleInvoke}
                  disabled={invoking}
                >
                  Invoke
                </Button>
              </Box>
              {response && (
                <Box>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                    <Typography variant='subtitle2'>Result</Typography>
                    <Chip
                      label={response.successful ? 'success' : 'error'}
                      color={response.successful ? 'success' : 'error'}
                      size='small'
                    />
                  </Box>
                  {response.successful ? (
                    <Box component='pre' sx={{ m: 0, p: 1, bgcolor: 'action.hover', borderRadius: 1, overflowX: 'auto', fontSize: 12 }}>
                      {response.result}
                    </Box>
                  ) : (
                    <Alert severity='warning'>{response.error}</Alert>
                  )}
                </Box>
              )}
            </Paper>
          ) : (
            <Paper variant='outlined' sx={{ p: 2 }}>
              <Typography color='text.secondary'>Select a tool to inspect and invoke it.</Typography>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default McpPlaygroundPage;
