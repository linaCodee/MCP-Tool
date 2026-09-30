import { Box, CircularProgress,Pagination, Typography } from '@mui/material';
import { useState } from 'react';
import type { ResourceFilter } from '../../../../api/types/resource.ts';
import useResources from '../../../../hooks/useResources.ts';
import ResourceFilters from '../../../components/resource/ResourceFilters/ResourceFilters.tsx';
import ResourceGrid from '../../../components/resource/ResourceGrid/ResourceGrid.tsx';

/**
 * The collected-resource browser.
 * TODO(student): Implement useResources, ResourceFilters and ResourceCard, and
 * add pagination controls (the backend endpoint is already paged).
 */
const ResourcesPage = () => {
  const [filter, setFilter] = useState<ResourceFilter>({});
  const [page,setPage] = useState<number>(0);

  const { resources, loading, onDelete, onAnalyze } = useResources(filter, page, 12);

  return (
    <Box>
      <Typography variant='h5' sx={{ mb: 2 }}>Resources</Typography>
      <ResourceFilters filter={filter} onChange={setFilter}/>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress/>
        </Box>
      )}
      {!loading && (!resources || resources.content.length === 0) && (
        <Typography color='text.secondary'>
          No resources yet. Run a search first.
        </Typography>
      )}
      {!loading && resources && (
          <>
          <ResourceGrid resources={resources.content} onDelete={onDelete} onAnalyze={onAnalyze}/>
              {resources.totalPages>1 && (
                  <Box sx={{display:'flex',justifyContent:'center',mt:3}}>
                      <Pagination count={resources.totalPages} page={page+1} onChange={(_,value)=>setPage(value-1)}></Pagination>
                  </Box>
              )}
          </>
          )}
    </Box>
  );
};

export default ResourcesPage;
