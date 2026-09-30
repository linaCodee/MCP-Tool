import {Box, Typography} from '@mui/material';
import type { ResourceAnalysisResponse } from '../../../../api/types/resource.ts';

interface ResourceAnalysisPanelProps {
  analysis: ResourceAnalysisResponse | null;
  onAnalyse: () => Promise<void>;
}

/**
 * TODO(student): Render the analysis of a resource: the summary, the keywords,
 * the sentence/word counts and the Macedonian-language confidence. Show a
 * call-to-action to run the analysis when it is still null.
 */
const ResourceAnalysisPanel = ({ analysis }: ResourceAnalysisPanelProps) => {

    if (!analysis){
        return (
            <Box sx={{mt:2}}>
                <Typography color='text.secondary' sx={{mb:1}}>
                    This resource has not been analyzed yet.
                </Typography>
            </Box>
        );
    }

    return (
        <Box sx={{mt:3}}>
            <Typography variant='h5' gutterBottom>Analysis</Typography>
            <Typography variant='subtitle2'>
                Summary
            </Typography>
            <Typography variant='body2' sx={{mb:2}}>
                {analysis.summary ?? 'No summary available.'}
            </Typography>
            <Typography variant='subtitle2'>
                Keywords
            </Typography>
            <Typography variant='body2' sx={{mb:2}}>
                {analysis.keywords ?? 'No keywords available.'}
            </Typography>
            <Typography variant='subtitle2'>
                Sentence count: {analysis.sentenceCount ?? 'N/A'}
            </Typography>
            <Typography variant='body2' sx={{mb:2}}>
                Macedonian confidence: {analysis.macedonianConfidence!=null ? `${Math.round(analysis.macedonianConfidence*100)}%` : 'N/A'}
            </Typography>
            <Typography variant='body2' sx={{mb:2}}>
                Analyzed at: {analysis.analyzedAt ? new Date(analysis.analyzedAt).toLocaleString() : 'N/A'}
            </Typography>
        </Box>
    )

};

export default ResourceAnalysisPanel;
