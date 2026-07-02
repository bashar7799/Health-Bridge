package com.example.data.repository

import com.example.data.local.ConsultationCaseDao
import com.example.data.local.DoctorDao
import com.example.data.local.PatientNotificationDao
import com.example.data.model.ConsultationCase
import com.example.data.model.Doctor
import com.example.data.model.PatientNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MedicalRepository(
    private val doctorDao: DoctorDao,
    private val consultationCaseDao: ConsultationCaseDao,
    private val patientNotificationDao: PatientNotificationDao
) {
    val activeDoctors: Flow<List<Doctor>> = doctorDao.getActiveDoctors()
    val allCases: Flow<List<ConsultationCase>> = consultationCaseDao.getAllCases()
    val allNotifications: Flow<List<PatientNotification>> = patientNotificationDao.getAllNotifications()

    fun getCasesForDoctor(doctorId: Int): Flow<List<ConsultationCase>> {
        return consultationCaseDao.getCasesForDoctor(doctorId)
    }

    suspend fun getDoctorById(id: Int): Doctor? {
        return doctorDao.getDoctorById(id)
    }

    suspend fun getCaseById(id: Int): ConsultationCase? {
        return consultationCaseDao.getCaseById(id)
    }

    suspend fun insertCase(consultationCase: ConsultationCase) {
        consultationCaseDao.insertCase(consultationCase)
    }

    suspend fun updateCase(consultationCase: ConsultationCase) {
        consultationCaseDao.updateCase(consultationCase)
    }

    suspend fun deleteCaseById(id: Int) {
        consultationCaseDao.deleteCaseById(id)
    }

    suspend fun insertNotification(notification: PatientNotification) {
        patientNotificationDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Int) {
        patientNotificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        patientNotificationDao.markAllAsRead()
    }

    suspend fun deleteNotificationById(id: Int) {
        patientNotificationDao.deleteNotificationById(id)
    }

    suspend fun clearAllNotifications() {
        patientNotificationDao.clearAllNotifications()
    }

    suspend fun seedDoctorsIfEmpty() {
        val doctors = doctorDao.getActiveDoctors().first()
        if (doctors.isEmpty()) {
            val defaultDoctors = listOf(
                Doctor(
                    name = "أ.د. عادل منصور",
                    specialty = "جراحة القلب المفتوح والأوعية الدموية (كبار وأطفال)",
                    background = "أستاذ مشارك جراحة القلب والصدر بجامعة صنعاء. خبرة أكثر من 20 عاماً في عمليات القلب المفتوح المعقدة للأطفال والكبار، وعلاج العيوب الخلقية في القلب وتضيّق الصمامات والأبهر.",
                    isAvailable = true
                ),
                Doctor(
                    name = "د. ليلى الشارحي",
                    specialty = "جراحة الصدر والرئة ومناظير الصدر الدقيقة",
                    background = "استشارية جراحة الصدر ومناظير الشعب الهوائية والرئة. زمالة الكلية الملكية للجراحين ببريطانيا. متخصصة في تشخيص وعلاج أورام الصدر، استسقاء الرئة، ومناظير القصبة الهوائية وتجويف الصدر.",
                    isAvailable = false
                ),
                Doctor(
                    name = "د. طارق الحيمي",
                    specialty = "أمراض وزراعة الكلى والمسالك البولية والذكورة",
                    background = "استشاري أول أمراض وزراعة الكلى والمسالك البولية. حاصل على البورد العربي والزمالة الأردنية. خبرة طويلة في علاج الفشل الكلوي المزمن، تفتيت الحصوات المعقدة، والعمليات الجراحية للمسالك.",
                    isAvailable = true
                ),
                Doctor(
                    name = "د. منى عبد الواسع",
                    specialty = "طب الأطفال، حديثي الولادة والأمراض الوراثية",
                    background = "استشارية طب الأطفال ورعاية المبتسرين وعناية الأطفال المركزة. دكتوراه طب الأطفال بجامعة القاهرة (قصر العيني). متخصصة في حالات الولادة المبكرة، العيوب الخلقية، ومتابعة النمو والتغذية العلاجية.",
                    isAvailable = true
                ),
                Doctor(
                    name = "د. محمد الريمي",
                    specialty = "جراحة المخ والأعصاب والعمود الفقري ومناظير الدماغ",
                    background = "استشاري جراحة الجملة العصبية والعمود الفقري. الدكتوراه والزمالة من جامعة القاهرة. متخصص في عمليات استئصال أورام الدماغ والنخاع الشوكي، وتثبيت الفقرات وعلاج انزلاق الديسك بالمناظير.",
                    isAvailable = false
                ),
                Doctor(
                    name = "د. خالد السقاف",
                    specialty = "أورام الكبار والعلاج الكيميائي والإشعاعي الموجه",
                    background = "استشاري أول علاج الأورام السرطانية والطب النووي. حاصل على البورد الفرنسي والزمالة الأوروبية لعلاج الأورام. خبير في وضع بروتوكولات العلاج الكيميائي والمناعي والهرموني الدقيق لمختلف الأورام.",
                    isAvailable = false
                ),
                Doctor(
                    name = "أ.د. سمير البعداني",
                    specialty = "أمراض الباطنة العامة والغدد الصماء والسكري",
                    background = "استشاري أول الأمراض الباطنية والغدد الصماء. حاصل على الزمالة البريطانية والبورد العربي. خبرة تفوق 15 عاماً في تشخيص وعلاج السكري، اضطرابات الغدة الدرقية، وأمراض الكبد والجهاز الهضمي والمناعة الذاتية.",
                    isAvailable = true
                )
            )
            doctorDao.insertDoctors(defaultDoctors)
        }
    }
}
