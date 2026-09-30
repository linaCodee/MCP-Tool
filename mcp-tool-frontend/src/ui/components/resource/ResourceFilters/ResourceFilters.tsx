import {
    Box,
    Slider,
    Stack,
    FormControl,
    TextField,
    Typography,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import type { ResourceFilter } from '../../../../api/types/resource.ts';

interface ResourceFiltersProps {
  filter: ResourceFilter;
  onChange: (filter: ResourceFilter) => void;
}

/**
 * TODO(student): Implement the filter bar for the resource browser: search run,
 * minimum Macedonian confidence (slider), analyzed yes/no, donated yes/no, and
 * a free-text search over the content. Call onChange with the updated filter.
 */

const ResourceFilters = ({ filter, onChange }: ResourceFiltersProps) => {


  return (
    <Box sx={{ mb: 2 }}>
      <Stack spacing={2}>
          <TextField label='Search' size='small' value={filter.search ?? ''} onChange={(event)=>onChange({
              ...filter,search: event.target.value || undefined
          })}/>
          <TextField label='Search Run ID' type='number' size='small' value={filter.searchRunId ?? ''} onChange={(event)=>onChange({...filter,searchRunId: event.target.value ? Number(event.target.value) : undefined})}/>
          <Box>
              <Typography variant='body2' gutterBottom>
                  Minimum Macedonian Confidence: {Math.round((filter.minMacedonianConfidence ?? 0)*100)}%
              </Typography>
              <Slider
                  value={(filter.minMacedonianConfidence ?? 0) * 100}
                  min={0}
                  max={100}
                  step={10}
                  valueLabelDisplay='auto'
                  onChange={(_, value) =>
                      onChange({
                          ...filter,
                          minMacedonianConfidence:
                              (value as number) / 100
                      })
                  }
              />
          </Box>
          <FormControl size='small'>
              <InputLabel>Analyzed</InputLabel>
              <Select label='Analyzed' value={filter.analyzed===undefined ? '' : String(filter.analyzed)} onChange={(event)=>{const value=event.target.value; onChange({...filter,analyzed: value==='' ? undefined : value==='true'});}}>
                  <MenuItem value='true'>Yes</MenuItem>
                  <MenuItem value='false'>No</MenuItem>
              </Select>
          </FormControl>
          <FormControl size='small'>
              <InputLabel>Donated</InputLabel>
              <Select label='Donated' value={filter.donated===undefined ? '' : String(filter.donated)} onChange={(event)=>{const value=event.target.value; onChange({...filter,donated:value==='' ? undefined : value==='true'});}}>
                  <MenuItem value='true'>Yes</MenuItem>
                  <MenuItem value='false'>No</MenuItem>
              </Select>
          </FormControl>
      </Stack>
    </Box>
  );
};

export default ResourceFilters;
