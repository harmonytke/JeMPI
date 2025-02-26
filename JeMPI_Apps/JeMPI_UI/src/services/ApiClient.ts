import { AxiosRequestConfig } from 'axios'
import { config } from '../config'
import { AuditTrailEntries } from '../types/AuditTrail'
import { FieldChangeReq, Fields } from '../types/Fields'
import {
  ApiSearchResponse,
  ApiSearchResult,
  CustomSearchQuery,
  FilterQuery,
  SearchQuery
} from '../types/SimpleSearch'
import { OAuthParams, User } from '../types/User'
import ROUTES from './apiRoutes'
import axiosInstance from './axios'
import moxios from './mockBackend'
import {
  NotificationResponse,
  Interaction,
  ExpandedGoldenRecordResponse,
  InteractionWithScore,
  NotificationRequest,
  LinkRequest,
  GoldenRecordCandidatesResponse
} from 'types/BackendResponse'
import {
  GoldenRecord,
  AnyRecord,
  DemographicData,
  PatientRecord
} from 'types/PatientRecord'
import { Notifications } from 'types/Notification'

const client = config.shouldMockBackend ? moxios : axiosInstance

class ApiClient {
  async getFields() {
    const { data } = await client.get<Fields>(ROUTES.GET_FIELDS_CONFIG)
    return data
  }

  async getMatches(
    limit: number,
    offset: number,
    created: string,
    state: string
  ): Promise<Notifications> {
    const url = `${ROUTES.GET_NOTIFICATIONS}?limit=${limit}&date=${created}&offset=${offset}&state=${state}`
    const { data } = await client.get<NotificationResponse>(url)
    const { records, skippedRecords, count } = data

    const formattedRecords = records.map(record => ({
      ...record,
      created: new Date(record.created)
    }))

    const pagination = {
      total: count + skippedRecords
    }

    return {
      records: formattedRecords,
      pagination
    }
  }

  async getInteraction(uid: string) {
    const { data } = await client.get<Interaction>(
      `${ROUTES.GET_INTERACTION}/${uid}`
    )
    return data
  }

  async getGoldenRecord(uid: string): Promise<GoldenRecord> {
    const {
      data: { goldenRecord, interactionsWithScore }
    } = await client.get<ExpandedGoldenRecordResponse>(
      `${ROUTES.GET_GOLDEN_RECORD}/${uid}`
    )
    return {
      uid: goldenRecord.uid,
      demographicData: goldenRecord.demographicData,
      sourceId: goldenRecord.sourceId,
      type: 'Current',
      createdAt: goldenRecord.uniqueGoldenRecordData.auxDateCreated,
      auxId: goldenRecord.uniqueGoldenRecordData.auxId,
      linkRecords: interactionsWithScore.map(({ interaction, score }) => ({
        uid: interaction.uid,
        sourceId: interaction.sourceId,
        createdAt: interaction.uniqueInteractionData.auxDateCreated,
        auxId: interaction.uniqueInteractionData.auxId,
        score,
        demographicData: interaction?.demographicData
      }))
    }
  }

  //TODO Move this logic to the backend and just get match details by notification ID
  async getMatchDetails(
    goldenId: string,
    candidateIds: string[]
  ): Promise<[GoldenRecord, GoldenRecord[]]> {
    const [goldenRecord, candidateRecords] = await Promise.all([
      this.getGoldenRecord(goldenId),
      this.getExpandedGoldenRecords(candidateIds)
    ])
    return [
      {
        ...goldenRecord,
        type: 'Current'
      },
      candidateRecords.map(r => ({
        ...r,
        type: 'Blocked'
      }))
    ]
  }

  async updateNotification(request: NotificationRequest) {
    const { data } = await client.post(ROUTES.POST_UPDATE_NOTIFICATION, request)
    return data
  }

  async newGoldenRecord(request: LinkRequest) {
    const url = `${ROUTES.PATCH_IID_NEW_GID_LINK}?goldenID=${request.goldenID}&patientID=${request.patientID}`
    const { data } = await client.patch(url)
    return data
  }

  async linkRecord(linkRequest: LinkRequest) {
    const url = `${ROUTES.PATCH_IID_GID_LINK}?goldenID=${linkRequest.goldenID}&newGoldenID=${linkRequest.newGoldenID}&patientID=${linkRequest.patientID}&score=2`
    const { data } = await client.patch(url)
    return data
  }

  async searchQuery(
    request: CustomSearchQuery | SearchQuery,
    isGoldenOnly: boolean
  ): Promise<ApiSearchResult<AnyRecord>> {
    const isCustomSearch = '$or' in request
    const endpoint = `${
      isCustomSearch ? ROUTES.POST_CUSTOM_SEARCH : ROUTES.POST_SIMPLE_SEARCH
    }/${isGoldenOnly ? 'golden' : 'patient'}`
    const { data: querySearchResponse } = await client.post(endpoint, request)
    if (isGoldenOnly) {
      const { pagination, data } =
        querySearchResponse as ApiSearchResponse<ExpandedGoldenRecordResponse>
      const result: ApiSearchResult<GoldenRecord> = {
        records: {
          data: data.map(({ goldenRecord, interactionsWithScore }) => ({
            uid: goldenRecord.uid,
            demographicData: goldenRecord.demographicData,
            sourceId: goldenRecord.sourceId,
            createdAt: goldenRecord.uniqueGoldenRecordData.auxDateCreated,
            auxId: goldenRecord.uniqueGoldenRecordData.auxId,
            linkRecords: interactionsWithScore.map(
              ({ interaction, score }) => ({
                uid: interaction.uid,
                sourceId: interaction.sourceId,
                createdAt: interaction.uniqueInteractionData.auxDateCreated,
                auxId: interaction.uniqueInteractionData.auxId,
                score,
                demographicData: interaction?.demographicData
              })
            )
          })),
          pagination: {
            total: pagination.total
          }
        }
      }
      return result
    } else {
      const { pagination, data } =
        querySearchResponse as ApiSearchResponse<Interaction>
      const result: ApiSearchResult<PatientRecord> = {
        records: {
          data: data.map((interaction: Interaction) => ({
            uid: interaction.uid,
            sourceId: interaction.sourceId,
            createdAt: interaction.uniqueInteractionData.auxDateCreated,
            auxId: interaction.uniqueInteractionData.auxId,
            demographicData: interaction?.demographicData
          })),
          pagination: {
            total: pagination.total
          }
        }
      }
      return result
    }
  }

  async getFilteredGoldenIds(request: FilterQuery) {
    const { data } = await client.post<{
      data: string[]
      pagination: { total: number }
    }>(ROUTES.POST_FILTER_GIDS, request)
    return data
  }

  async getFilteredGoldenIdsWithInteractionCount(request: FilterQuery) {
    const {
      data: { data, interactionCount, pagination }
    } = await client.post<{
      data: string[]
      interactionCount: { total: number }
      pagination: { total: number }
    }>(ROUTES.POST_FILTER_GIDS_WITH_INTERACTION_COUNT, request)
    const total = pagination.total + interactionCount.total
    return {
      data,
      pagination: { total }
    }
  }

  async getExpandedGoldenRecords(
    goldenIds: Array<string> | undefined
  ): Promise<GoldenRecord[]> {
    const { data } = await client.get<Array<ExpandedGoldenRecordResponse>>(
      ROUTES.GET_EXPANDED_GOLDEN_RECORDS,
      {
        params: { uidList: goldenIds?.toString() }
      }
    )
    const records: GoldenRecord[] = data.map(
      ({ goldenRecord, interactionsWithScore }) => {
        return {
          demographicData: goldenRecord.demographicData,
          uid: goldenRecord.uid,
          createdAt: goldenRecord.uniqueGoldenRecordData.auxDateCreated,
          sourceId: goldenRecord.sourceId,
          type: 'Current',
          auxId: goldenRecord.uniqueGoldenRecordData.auxId,
          linkRecords: interactionsWithScore
            .map(({ interaction, score }: InteractionWithScore) => ({
              demographicData: interaction.demographicData,
              uid: interaction.uid,
              sourceId: interaction.sourceId,
              createdAt: interaction.uniqueInteractionData.auxDateCreated,
              auxId: interaction.uniqueInteractionData.auxId,
              score
            }))
            .sort(
              (objA, objB) => Number(objA.createdAt) - Number(objB.createdAt)
            )
        }
      }
    )

    return records
  }

  async getFlatExpandedGoldenRecords(
    goldenIds: Array<string> | undefined
  ): Promise<AnyRecord[]> {
    const goldenRecords = await this.getExpandedGoldenRecords(goldenIds)
    return goldenRecords.reduce((acc: Array<AnyRecord>, record) => {
      acc.push(record, ...record.linkRecords)
      return acc
    }, [])
  }

  async getCandidates(
    demographicData: DemographicData,
    candidateThreshold: number
  ): Promise<GoldenRecord[]> {
    const { data } = await client.post<GoldenRecordCandidatesResponse>(
      ROUTES.POST_CR_CANDIDATES,
      { demographicData, candidateThreshold }
    )

    return data.goldenRecords?.map(record => ({
      demographicData: record.demographicData,
      uid: record.goldenId,
      sourceId: record.sourceId,
      createdAt: record.customUniqueGoldenRecordData.auxDateCreated,
      auxId: record.customUniqueGoldenRecordData.auxId,
      score: candidateThreshold,
      type: 'Blocked',
      linkRecords: []
    }))
  }

  async getGoldenRecordAuditTrail(gid: string) {
    const {
      data: { entries }
    } = await client.get<AuditTrailEntries>(
      ROUTES.GET_GOLDEN_RECORD_AUDIT_TRAIL,
      {
        params: {
          gid
        }
      }
    )
    return entries
  }

  async getInteractionAuditTrail(iid: string) {
    const {
      data: { entries }
    } = await client.get<AuditTrailEntries>(
      ROUTES.GET_INTERACTION_AUDIT_TRAIL,
      {
        params: {
          iid
        }
      }
    )
    return entries
  }

  async validateOAuth(oauthParams: OAuthParams) {
    const { data } = await client.post(ROUTES.VALIDATE_OAUTH, oauthParams)
    return data as User
  }

  async getCurrentUser() {
    const { data } = await client.get(ROUTES.CURRENT_USER)
    return data
  }

  async logout() {
    return await client.get(ROUTES.LOGOUT)
  }

  async updatedGoldenRecord(uid: string, request: FieldChangeReq) {
    const response = await client.patch(
      `${ROUTES.PATCH_GOLDEN_RECORD}/${uid}`,
      request
    )
    return response
  }

  uploadFile = async (requestConfig: AxiosRequestConfig<FormData>) => {
    const { data } = await client.post(
      ROUTES.UPLOAD,
      requestConfig.data,
      requestConfig
    )
    return data
  }
}

export default new ApiClient()
