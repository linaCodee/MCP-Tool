import { Box, Chip, CircularProgress, Divider, Link, List, ListItem, ListItemText, Paper, Typography } from '@mui/material';
import { useParams } from 'react-router';
import useSearchRunDetails from '../../../../hooks/useSearchRunDetails.ts';
import ToolLogViewer from '../../../components/searchrun/ToolLogViewer/ToolLogViewer.tsx';

/**
 * One search run in detail: its status, the resources it produced, and the
 * live MCP tool-invocation trace. Provided as the reference for a detail page.
 */
const SearchRunDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const { searchRun, resources, logs, loading } = useSearchRunDetails(id!);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress/>
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <Box>
        <Typography variant='h5' gutterBottom>
          Search Run #{id}
        </Typography>
        {searchRun && (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant='subtitle1'>“{searchRun.query}”</Typography>
            <Chip label={searchRun.status} size='small'/>
            <Typography variant='body2' color='text.secondary'>
              {searchRun.resultCount ?? 0} resource(s)
            </Typography>
          </Box>
        )}
      </Box>

      <Paper variant='outlined' sx={{ p: 2 }}>
        <Typography variant='h6' gutterBottom>Resources</Typography>
        {resources.length === 0 && (
          <Typography color='text.secondary'>No resources for this run.</Typography>
        )}
        <List dense>
          {resources.map((resource) => (
            <ListItem key={resource.id} divider>
              <ListItemText
                primary={resource.title ?? `Resource #${resource.id}`}
                secondary={
                  resource.sourceUrl
                    ? <Link href={resource.sourceUrl} target='_blank' rel='noreferrer'>{resource.sourceUrl}</Link>
                    : null
                }
              />
              {resource.macedonianConfidence != null && (
                <Chip
                  label={`MK ${(resource.macedonianConfidence * 100).toFixed(0)}%`}
                  size='small'
                  sx={{ ml: 1 }}
                />
              )}
            </ListItem>
          ))}
        </List>
      </Paper>

      <Paper variant='outlined' sx={{ p: 2 }}>
        <Typography variant='h6' gutterBottom>MCP tool trace</Typography>
        <Divider sx={{ mb: 1 }}/>
        <ToolLogViewer logs={logs}/>
      </Paper>
    </Box>
  );
};

export default SearchRunDetailsPage;
