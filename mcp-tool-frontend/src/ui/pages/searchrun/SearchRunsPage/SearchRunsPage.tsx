import { Box, Button, CircularProgress, Grid, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useState } from 'react';
import useSearchRuns from '../../../../hooks/useSearchRuns.ts';
import SearchRunCard from '../../../components/searchrun/SearchRunCard/SearchRunCard.tsx';
import RunSearchDialog from '../../../components/searchrun/RunSearchDialog/RunSearchDialog.tsx';

/**
 * The search control panel — the fully-worked reference feature. The data flow
 * (useSearchRuns -> SearchRunCard) and the "Run search" dialog are provided as
 * the pattern to mirror for the Resources and Donations features.
 */
const SearchRunsPage = () => {
  const { searchRuns, loading } = useSearchRuns();

  const [runDialogOpen, setRunDialogOpen] = useState<boolean>(false);

  return (
    <Box>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress/>
        </Box>
      )}
      {!loading &&
       <>
         <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
           <Typography variant='h5'>Search Runs</Typography>
           <Button variant='contained' startIcon={<AddIcon/>} onClick={() => setRunDialogOpen(true)}>
             Run Search
           </Button>
         </Box>
         {searchRuns.length === 0 && (
           <Typography color='text.secondary'>
             No search runs yet. Run a search to collect Macedonian resources
             from the assigned website.
           </Typography>
         )}
         <Grid container spacing={2}>
           {searchRuns.map((searchRun) => (
             <Grid key={searchRun.id} size={{ xs: 12, sm: 6, md: 4 }}>
               <SearchRunCard searchRun={searchRun}/>
             </Grid>
           ))}
         </Grid>
         <RunSearchDialog
           open={runDialogOpen}
           onClose={() => setRunDialogOpen(false)}
         />
       </>}
    </Box>
  );
};

export default SearchRunsPage;
